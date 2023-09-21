package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventDao;
import ru.practicum.request.dto.EventRequestStatusUpdateDataDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestDao;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserDao;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestDao requestDao;
    private final UserDao userDao;
    private final EventDao eventDao;

    @Override
    public RequestDto add(long userId, long eventId) {
        final User requester = userDao.getUser(userId);
        final Event event = eventDao.getEvent(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new IllegalStateException("Нельзя участвовать в неопубликованном событии!");
        }

        if (event.getInitiator().getId() == userId) {
            throw new IllegalStateException("Инициатор события не может добавить запрос на участие в своём событии!");
        }

        if (event.getParticipantLimit() != 0) {
            int participantCount = requestDao.getConfirmedRequestsCount(event.getId());

            if (participantCount == event.getParticipantLimit()) {
                throw new IllegalStateException("Уже достигнут лимит на кол-во участников события!");
            }
        }

        final RequestStatus newRequestStatus = !event.getIsModerationRequired() || event.getParticipantLimit() == 0 ? RequestStatus.CONFIRMED : RequestStatus.PENDING;

        Request newRequest = Request.builder()
                .requester(requester)
                .event(event)
                .status(newRequestStatus)
                .created(LocalDateTime.now())
                .build();
        newRequest = requestDao.save(newRequest);

        return RequestMapper.toRequestDto(newRequest);
    }

    @Override
    public EventRequestStatusUpdateResultDto changeStatus(long userId, long eventId, EventRequestStatusUpdateDataDto updateDataDto) {
        userDao.checkUserExists(userId);
        final Event event = eventDao.getEvent(eventId);

        final EventRequestStatusUpdateResultDto resultDto = new EventRequestStatusUpdateResultDto();

        if (updateDataDto.getRequestIds().isEmpty()) {
            resultDto.setConfirmedRequests(Collections.emptyList());
            resultDto.setRejectedRequests(Collections.emptyList());

            return resultDto;
        }

        // Получить заявки (получаем в порядке добавления).
        final List<Request> requestList = requestDao.getRequests(updateDataDto.getRequestIds(), Sort.by("created"));
        final RequestStatus status = updateDataDto.getStatus();

        if (status.equals(RequestStatus.CONFIRMED)) {
            if (event.getParticipantLimit() == requestDao.getConfirmedRequestsCount(eventId)) {
                throw new IllegalStateException("Нельзя принять заявки, если достигнут лимит участников!");
            }

            setRequestsStatus(requestList, RequestStatus.CONFIRMED);
            requestDao.saveAll(requestList);
        } else if (status.equals(RequestStatus.REJECTED)) {
            final boolean hasConfirmed = requestList.stream().anyMatch(r -> r.getStatus().equals(RequestStatus.CONFIRMED));
            if (hasConfirmed) {
                throw new IllegalStateException("Нельзя отменить уже подтвержденную заявку!");
            }

            setRequestsStatus(requestList, RequestStatus.REJECTED);
            requestDao.saveAll(requestList);
        }

        final List<Request> confirmed = requestDao.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        final List<Request> rejected = requestDao.findAllByEventIdAndStatus(eventId, RequestStatus.REJECTED);

        resultDto.setRejectedRequests(mapRequestsToDto(rejected));
        resultDto.setConfirmedRequests(mapRequestsToDto(confirmed));

        return resultDto;
    }

    @Override
    public RequestDto cancel(long userId, long requestId) {
        userDao.checkUserExists(userId);

        Request request = requestDao.getRequest(requestId);
        request.setStatus(RequestStatus.CANCELED);
        request = requestDao.save(request);

        return RequestMapper.toRequestDto(request);
    }

    // Получение информации о заявках текущего пользователя на участие в чужих событиях
    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getUserRequests(long userId) {
        userDao.checkUserExists(userId);

        final List<Request> userRequests = requestDao.getUserRequests(userId);
        return userRequests.stream().map(RequestMapper::toRequestDto).collect(toUnmodifiableList());
    }

    // Получение информации о заявках на участие событиях текущего пользователя
    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getRequestsInUserEvent(long userId, long eventId) {
        userDao.checkUserExists(userId);
        eventDao.checkEventExists(eventId);

        final List<Request> userRequests = requestDao.getRequestsInUserEvent(userId, eventId);
        return userRequests.stream().map(RequestMapper::toRequestDto).collect(toUnmodifiableList());
    }

    private void setRequestsStatus(Iterable<Request> requests, RequestStatus status) {
        for (Request r : requests) {
            r.setStatus(status);
        }
    }

    private List<RequestDto> mapRequestsToDto(List<Request> requests) {
        return requests.stream().map(RequestMapper::toRequestDto).collect(toUnmodifiableList());
    }
}
