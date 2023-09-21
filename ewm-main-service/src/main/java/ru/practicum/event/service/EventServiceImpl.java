package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryDao;
import ru.practicum.common.OffsetPageableValidator;
import ru.practicum.common.StatsClientWrapper;
import ru.practicum.common.Utils;
import ru.practicum.common.ValidationParams;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.dto.EndpointStatsDto;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.*;
import ru.practicum.event.repository.EventDao;
import ru.practicum.request.repository.RequestDao;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserDao;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventDao eventDao;
    private final UserDao userDao;
    private final CategoryDao categoryDao;
    private final RequestDao requestDao;
    private final StatsClientWrapper statsClientWrapper;

    @Override
    @Transactional
    public EventFullDto add(long userId, EventCreateDto eventCreateDto) {
        final User initiator = userDao.getUser(userId);
        final Category category = categoryDao.getCategory(eventCreateDto.getCategoryId());

        validateEventDate(eventCreateDto.getEventDate());
        setDefaults(eventCreateDto);

        Event newEvent = EventMapper.toEvent(eventCreateDto);
        newEvent.setInitiator(initiator);
        newEvent.setCategory(category);
        newEvent.setCreatedOn(LocalDateTime.now());
        newEvent.setState(EventState.PENDING);

        newEvent = eventDao.save(newEvent);

        return EventMapper.toEventFullDto(newEvent);
    }

    @Override
    @Transactional
    public EventFullDto edit(long userId, long eventId, EventUpdateByUserDto eventUpdateDto) {
        validateNotNullFields(eventUpdateDto);

        Event eventFromDB = eventDao.getEventByUser(eventId, userId);
        validateEventStateForUpdate(eventFromDB.getState());

        // Обновление.
        EventMapper.updateIfDifferent(eventFromDB, eventUpdateDto);

        // Устанавливаем новое состояние.
        if (nonNull(eventUpdateDto.getStateAction())) {
            final EventState newState = mapToEventState(eventUpdateDto.getStateAction());
            eventFromDB.setState(newState);
        }

        // Категорию меняю вручную.
        if (nonNull(eventUpdateDto.getCategoryId())) {
            final Category category = categoryDao.getCategory(eventUpdateDto.getCategoryId());
            eventFromDB.setCategory(category);
        }

        eventFromDB = eventDao.save(eventFromDB);

        return EventMapper.toEventFullDto(eventFromDB);
    }

    @Override
    @Transactional
    public EventFullDto edit(long eventId, EventUpdateByAdminDto eventUpdateDto) {
        validateNotNullFields(eventUpdateDto);

        Event eventFromDB = eventDao.getEvent(eventId);
        validateEventStateForUpdate(eventFromDB.getState());

        // Обновление.
        EventMapper.updateIfDifferent(eventFromDB, eventUpdateDto);

        // Устанавливаем новое состояние.
        final EventState oldState = eventFromDB.getState();
        if (nonNull(eventUpdateDto.getStateAction())) {
            final EventState newState = mapToEventState(eventUpdateDto.getStateAction());
            if (oldState.equals(EventState.CANCELED) && newState.equals(EventState.PUBLISHED)) {
                throw new IllegalStateException("Нельзя опубликовать отмененное событие!");
            }
            if (oldState.equals(EventState.PUBLISHED) && newState.equals(EventState.CANCELED)) {
                throw new IllegalStateException("Нельзя отменить опубликованное событие!");
            }
            eventFromDB.setState(newState);

            if (newState.equals(EventState.PUBLISHED)) {
                eventFromDB.setPublishedOn(LocalDateTime.now());
            }
        }

        // Категорию меняю вручную.
        if (nonNull(eventUpdateDto.getCategoryId())) {
            final Category category = categoryDao.getCategory(eventUpdateDto.getCategoryId());
            eventFromDB.setCategory(category);
        }

        eventFromDB = eventDao.save(eventFromDB);

        return EventMapper.toEventFullDto(eventFromDB);
    }

    @Override
    public EventFullDto getEventByUser(long userId, long eventId) {
        final Event event = eventDao.getEventByUser(eventId, userId);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEvent(long id) {
        final Event event = eventDao.getEvent(id);

        // Получение и установка данных из статистики и заявок.
        addViewsAndConfirmedRequests(event);

        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventOnlyIfPublished(long id) {
        final Event event = eventDao.getEvent(id);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException(String.format("Событие с id = %s не найдено!", id));
        }

        // Получение и установка данных из статистики и заявок.
        addViewsAndConfirmedRequests(event);

        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> search(List<Long> userIds,
                                     List<EventState> states,
                                     List<Long> categoryIds,
                                     LocalDateTime start,
                                     LocalDateTime end,
                                     Integer from,
                                     Integer size) {
        ValidationParams.validateStartEndDate(start, end);

        final Pageable pageable = OffsetPageableValidator.validateAndGet(from, size);
        final List<Event> searchedEvents = eventDao.searchEvents(userIds, states, categoryIds, start, end, pageable);

        if (searchedEvents.isEmpty()) {
            return Collections.emptyList();
        }

        // Получение и установка данных из статистики и заявок.
        addViewsAndConfirmedRequests(searchedEvents);

        return searchedEvents.stream().map(EventMapper::toEventFullDto).collect(toUnmodifiableList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getPublishedEvents(String text,
                                                  List<Long> categoryIds,
                                                  Boolean paid,
                                                  LocalDateTime start,
                                                  LocalDateTime end,
                                                  Boolean onlyAvailable,
                                                  EventSort sort,
                                                  Integer from,
                                                  Integer size) {
        ValidationParams.validateStartEndDate(start, end);

        final Pageable pageable = OffsetPageableValidator.validateAndGet(from, size);
        // если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события, которые произойдут позже текущей даты и времени
        if (isNull(start) && isNull(end)) {
            start = LocalDateTime.now();
        }

        List<Event> filteredEvents = eventDao.getPublishedEvents(text, categoryIds, paid, start, end, sort, pageable);

        // Получение и установка данных из статистики и заявок.
        addViewsAndConfirmedRequests(filteredEvents);

        // Сами отсеиваем те события на которых достигнут лимит по участникам.
        if (nonNull(onlyAvailable) && onlyAvailable) {
            filteredEvents = filteredEvents.stream()
                    .filter(e -> e.getParticipantLimit() == 0 || e.getConfirmedRequests() < e.getParticipantLimit())
                    .collect(toList());
        }

        // Сами сортируем по просмотрам.
        if (nonNull(sort) && sort.equals(EventSort.VIEWS)) {
            filteredEvents.sort(Comparator.comparingLong(Event::getViews));
        }

        return filteredEvents.stream().map(EventMapper::toEventShortDto).collect(toUnmodifiableList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsByUser(long userId, Integer from, Integer size) {
        userDao.checkUserExists(userId);

        final Pageable pageable = OffsetPageableValidator.validateAndGet(from, size);
        final List<Event> events = eventDao.getEventsByUser(userId, pageable);
        return events.stream().map(EventMapper::toEventFullDto).collect(toUnmodifiableList());
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (isNull(eventDate)) {
            return;
        }

        final long hoursDelay = 2L;

        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime minEventDate = now.plusHours(hoursDelay);

        if (eventDate.isBefore(minEventDate)) {
            throw new IllegalArgumentException("Время события не может быть раньше, чем через два часа от текущего момента!");
        }
    }

    private void validateEventStateForUpdate(EventState state) {
        if (!(state.equals(EventState.PENDING) || state.equals(EventState.CANCELED))) {
            throw new IllegalStateException("Изменять можно только отмененные события или события в состоянии ожидания модерации!");
        }
    }

    private void validateTitle(String title) {
        Utils.validateLengthOfNullableString(title, 3, 120);
    }

    private void validateAnnotation(String annotation) {
        Utils.validateLengthOfNullableString(annotation, 20, 2000);
    }

    private void validateDescription(String description) {
        Utils.validateLengthOfNullableString(description, 20, 7000);
    }

    private void validateNotNullFields(EventUpdateDto eventUpdateDto) {
        validateTitle(eventUpdateDto.getTitle());
        validateAnnotation(eventUpdateDto.getAnnotation());
        validateDescription(eventUpdateDto.getDescription());
        validateEventDate(eventUpdateDto.getEventDate());
    }

    private void setDefaults(EventCreateDto eventCreateDto) {
        if (isNull(eventCreateDto.getIsPaid())) {
            eventCreateDto.setIsPaid(false);
        }

        if (isNull(eventCreateDto.getIsModerationRequired())) {
            eventCreateDto.setIsModerationRequired(true);
        }

        if (isNull(eventCreateDto.getParticipantLimit())) {
            eventCreateDto.setParticipantLimit(0);
        }
    }

    private void addViewsAndConfirmedRequests(Event event) {
        if (isNull(event)) {
            return;
        }

        final long eventId = event.getId();

        event.setConfirmedRequests(requestDao.getConfirmedRequestsCount(eventId));
        final List<EndpointStatsDto> statsDtoList = getEventViewsStat(eventId);
        event.setViews(statsDtoList.isEmpty() ? 0 : statsDtoList.get(0).getHits());
    }

    private void addViewsAndConfirmedRequests(List<Event> events) {
        if (events.isEmpty()) {
            return;
        }

        final List<Long> eventIds = events.stream().map(Event::getId).collect(toUnmodifiableList());
        final Map<Long, Long> eventsConfirmedRequestsMap = requestDao.getConfirmedRequestsCountMap(eventIds);
        final Map<Long, Long> eventsViewsMap = getEventsViewsMap(eventIds);

        for (Event event : events) {
            event.setConfirmedRequests(eventsConfirmedRequestsMap.getOrDefault(event.getId(), 0L));
            event.setViews(eventsViewsMap.getOrDefault(event.getId(), 0L));
        }
    }

    private EventState mapToEventState(UpdateEventUserAction userAction) {
        EventState result = EventState.PENDING;

        switch (userAction) {
            case CANCEL_REVIEW: {
                result = EventState.CANCELED;
                break;
            }
            case SEND_TO_REVIEW: {
                result = EventState.PENDING;
                break;
            }
        }

        return result;
    }

    private EventState mapToEventState(UpdateEventAdminAction adminAction) {
        EventState result = EventState.PENDING;

        switch (adminAction) {
            case PUBLISH_EVENT: {
                result = EventState.PUBLISHED;
                break;
            }
            case REJECT_EVENT: {
                result = EventState.CANCELED;
                break;
            }
        }

        return result;
    }

    private List<EndpointStatsDto> getEventViewsStat(long id) {
        return getEventViewsStat(List.of(id));
    }

    private List<EndpointStatsDto> getEventViewsStat(List<Long> ids) {
        final List<String> eventUris = ids.stream().map(this::getEventUri).collect(toUnmodifiableList());
        return statsClientWrapper.getStats(null, null, eventUris, true);
    }

    private Map<Long, Long> getEventsViewsMap(List<Long> ids) {
        final Map<String, Long> uriIdMap = ids.stream().collect(toMap(this::getEventUri, id -> id));
        final List<EndpointStatsDto> viewStats = getEventViewsStat(ids);

        return viewStats.stream().collect(toMap(uriIdMap::get, EndpointStatsDto::getHits));
    }

    private String getEventUri(long id) {
        return "/events/" + id;
    }
}
