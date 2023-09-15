package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryDao;
import ru.practicum.common.OffsetPageableValidator;
import ru.practicum.common.StatsClientWrapper;
import ru.practicum.dto.EndpointStatsDto;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.*;
import ru.practicum.event.repository.EventDao;
import ru.practicum.request.repository.RequestDao;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserDao;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toUnmodifiableList;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventDao eventDao;
    private final UserDao userDao;
    private final CategoryDao categoryDao;
    private final RequestDao requestDao;
    private final StatsClientWrapper statsClientWrapper;

    @Override
    public EventFullDto add(long userId, EventCreateDto eventCreateDto) {
        final User initiator = userDao.getUser(userId);
        final Category category = categoryDao.getCategory(eventCreateDto.getCategory_id());

        validateEventDate(eventCreateDto.getEventDate());

        Event newEvent = EventMapper.toEvent(eventCreateDto);
        newEvent.setInitiator(initiator);
        newEvent.setCategory(category);
        newEvent.setCreatedOn(LocalDateTime.now());
        newEvent.setState(EventState.PENDING);

        newEvent = eventDao.save(newEvent);

        return EventMapper.ToEventFullDto(newEvent);
    }

    @Override
    public EventFullDto edit(long userId, long eventId, EventUpdateByUserDto eventUpdateDto) {
        validateEventDate(eventUpdateDto.getEventDate());

        Event eventFromDB = eventDao.getEventByUser(eventId, userId);

        validateEventStateForUpdate(eventFromDB.getState());

        // Обновление.
        EventMapper.updateIfDifferent(eventFromDB, eventUpdateDto);

        // Устанавливаем новое состояние.
        final EventState newState = mapToEventState(eventUpdateDto.getStateAction());
        eventFromDB.setState(newState);

        // Категорию меняю вручную.
        final Category category = categoryDao.getCategory(eventUpdateDto.getCategory_id());
        eventFromDB.setCategory(category);

        eventFromDB = eventDao.save(eventFromDB);

        // ToDo ?
        // Получение и установка данных из статистики и заявок.
        final EventFullDto eventFullDto = EventMapper.ToEventFullDto(eventFromDB);

        return eventFullDto;
    }

    @Override
    public EventFullDto edit(long eventId, EventUpdateByAdminDto eventUpdateDto) {
        validateEventDate(eventUpdateDto.getEventDate());

        Event eventFromDB = eventDao.getEvent(eventId);

        validateEventStateForUpdate(eventFromDB.getState());

        // Обновление.
        EventMapper.updateIfDifferent(eventFromDB, eventUpdateDto);

        // Устанавливаем новое состояние.
        final EventState newState = mapToEventState(eventUpdateDto.getStateAction());
        eventFromDB.setState(newState);

        // Категорию меняю вручную.
        final Category category = categoryDao.getCategory(eventUpdateDto.getCategory_id());
        eventFromDB.setCategory(category);

        eventFromDB = eventDao.save(eventFromDB);

        // ToDo ?
        // Получение и установка данных из статистики и заявок.
        final EventFullDto eventFullDto = EventMapper.ToEventFullDto(eventFromDB);

        return eventFullDto;
    }

    @Override
    public EventFullDto getEventByUser(long userId, long eventId) {
        final Event event = eventDao.getEventByUser(eventId, userId);
        final EventFullDto eventFullDto = EventMapper.ToEventFullDto(event);

        // ToDo ?
        // Получение и установка данных из статистики и заявок.

        return eventFullDto;
    }

    @Override
    public EventFullDto getEvent(long id) {
        final Event event = eventDao.getEvent(id);
        final EventFullDto eventFullDto = EventMapper.ToEventFullDto(event);

        // Получение и установка данных из статистики и заявок.
        eventFullDto.setConfirmedRequests(requestDao.getConfirmedRequestsCount(id));
        final List<EndpointStatsDto> statsDtoList = getEventViewsStat(id);
        eventFullDto.setViews(statsDtoList.get(0).getHits());

        return eventFullDto;
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
        final Pageable pageable = OffsetPageableValidator.validateAndGet(from, size);
        final List<Event> searchedEvents = eventDao.searchEvents(userIds, states, categoryIds, start, end, pageable);

        if (searchedEvents.isEmpty()) {
            return Collections.emptyList();
        }

        // ToDo ???
        // Получение и установка данных из статистики и заявок
//        final List<Long> eventIds = searchedEvents.stream().map(Event::getId).collect(toUnmodifiableList());
//        final Map<Long, Long> eventsConfirmedRequestsMap = requestDao.getConfirmedRequestsCount(eventIds);
//        final Map<Long, Long> eventsViewsMap = getEventsViewsMap(eventIds);
//
//        final List<EventFullDto> result = new ArrayList<>();
//        for (Event event : searchedEvents) {
//            final EventFullDto eventFullDto = EventMapper.ToEventFullDto(event);
//            eventFullDto.setConfirmedRequests(eventsConfirmedRequestsMap.getOrDefault(event.getId(), 0L));
//            eventFullDto.setViews(eventsViewsMap.getOrDefault(event.getId(), 0L));
//
//            result.add(eventFullDto);
//        }

        final List<EventFullDto> result = searchedEvents.stream().map(EventMapper::ToEventFullDto).collect(toUnmodifiableList());
        return result;
    }

    @Override
    public List<EventFullDto> getPublishedEvents(String text,
                                                 List<Long> categoryIds,
                                                 Boolean paid,
                                                 LocalDateTime start,
                                                 LocalDateTime end,
                                                 Boolean onlyAvailable,
                                                 EventSort sort,
                                                 Integer from,
                                                 Integer size) {
        final Pageable pageable = OffsetPageableValidator.validateAndGet(from, size);

        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsByUser(long userId, Integer from, Integer size) {
        userDao.checkUserExists(userId);

        final Pageable pageable = OffsetPageableValidator.validateAndGet(from, size);
        final List<Event> events = eventDao.getEventsByUser(userId, pageable);
        final List<EventFullDto> eventFullDtoList = events.stream().map(EventMapper::ToEventFullDto).collect(toUnmodifiableList());

        // ToDo ?
        // Получение и установка данных из статистики и заявок.

        return eventFullDtoList;
    }

    private void validateEventDate(LocalDateTime eventDate) {
        final long hoursDelay = 2L;

        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime minEventDate = now.plusHours(hoursDelay);

        if (eventDate.isBefore(minEventDate)) {
            throw new IllegalArgumentException("Время события не может быть раньше, чем через два часа от текущего момента!");
        }
    }

    public void validateEventStateForUpdate(EventState state) {
        if (!(state.equals(EventState.PENDING) || state.equals(EventState.CANCELED))) {
            throw new IllegalStateException("Изменять можно только отмененные события или события в состоянии ожидания модерации!");
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
        final Map<String, Long> uriIdMap = ids.stream().collect(Collectors.toMap(this::getEventUri, id -> id));
        final List<EndpointStatsDto> viewStats = getEventViewsStat(ids);

        return viewStats.stream().collect(Collectors.toMap(uriIdMap::get, EndpointStatsDto::getHits));
    }

    private String getEventUri(long id) {
        return "/events/" + id;
    }
}
