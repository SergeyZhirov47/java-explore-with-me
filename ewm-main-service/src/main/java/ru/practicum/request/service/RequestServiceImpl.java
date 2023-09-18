package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestDao requestDao;
    private final UserDao userDao;
    private final EventDao eventDao;

    @Override
    @Transactional
    public RequestDto add(long userId, long eventId) {
        final User requester = userDao.getUser(userId);
        final Event event = eventDao.getEvent(eventId);

        // ToDo
        // нельзя добавить повторный запрос (Ожидается код ошибки 409)
        // Самому проверять или переложить на БД?

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new IllegalStateException("Нельзя участвовать в неопубликованном событии!");
        }

        if (event.getInitiator().getId() == userId) {
            throw new IllegalStateException("Инициатор события не может добавить запрос на участие в своём событии!");
        }

        if (event.getParticipantLimit() != 0) {
            // сколько пользователей захотело участвовать (у кого заявка в рассмотрении или одобрена)
            int participantCount = requestDao.getParticipantCountInEvent(event.getId());

            if (participantCount == event.getParticipantLimit()) {
                throw new IllegalStateException("Уже достигнут лимит на кол-во участников события!");
            }
        }

        // event.getParticipantLimit() == 0
        // final RequestStatus newRequestStatus = event.isModerationRequired() ? RequestStatus.PENDING : RequestStatus.CONFIRMED;
        final RequestStatus newRequestStatus = event.getParticipantLimit() == 0 || !event.getIsModerationRequired() ? RequestStatus.CONFIRMED : RequestStatus.PENDING;

//        if (event.isModerationRequired()) {
//            if (event.getParticipantLimit() == 0) {
//                newRequestStatus = RequestStatus.CONFIRMED;
//            }
//            newRequestStatus = RequestStatus.PENDING;
//        } else {
//            newRequestStatus = RequestStatus.CONFIRMED;
//        }

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
    @Transactional
    public EventRequestStatusUpdateResultDto changeStatus(long userId, long eventId, EventRequestStatusUpdateDataDto updateDataDto) {
        userDao.checkUserExists(userId);
        final Event event = eventDao.getEvent(eventId);

        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();
        final EventRequestStatusUpdateResultDto resultDto = new EventRequestStatusUpdateResultDto();

        if (updateDataDto.getRequestIds().isEmpty()) {
            resultDto.setConfirmedRequests(Collections.emptyList());
            resultDto.setRejectedRequests(Collections.emptyList());

            return resultDto;
        }

        // Получить заявки (получаем в порядке добавления).
        final List<Request> requestList = requestDao.getRequests(updateDataDto.getRequestIds(), Sort.by("created"));
        final RequestStatus status = updateDataDto.getStatus();

        // ToDo
        // Может быть, что часть или всех заявок не будет (неверные id).
        // Что будет делать? В любом случае исключение (если requestList.size() != updateDataDto.getRequestIds().size())

        // статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
//        final boolean isAllRequestsHasPendingStatus = requestList.stream().anyMatch(r -> !r.getStatus().equals(RequestStatus.PENDING));
//        if (!isAllRequestsHasPendingStatus) {
//            throw new IllegalStateException("Статус можно изменить только у заявок, находящихся в состоянии ожидания!");
//        }

        // если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
        if (event.getParticipantLimit() == 0) {
            // Все утверждаем.
            setRequestsStatus(requestList, RequestStatus.CONFIRMED);
            confirmed = requestDao.saveAll(requestList);

            resultDto.setRejectedRequests(Collections.emptyList());
            resultDto.setConfirmedRequests(mapRequestsToDto(confirmed));

            return resultDto;
        }

        if (status.equals(RequestStatus.CONFIRMED)) {
            // сколько пользователей захотело участвовать (у кого заявка в рассмотрении или одобрена)
            int participantCount = requestDao.getParticipantCountInEvent(event.getId());

            // нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
//            if (participantCount == event.getParticipantLimit()) {
//                throw new IllegalStateException("Заявки нельзя утвердить. Достигнут лимит по заявкам на данное событие!");
//            }

            // если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
            // ToDo
            // неподтверждённые отклонить? - это только те, которые в текущем запросе пришли или вообще все неподтвержденные для данного события?

            // теперь рассчитываем сколько можем подтвердить, а остальные отклоняем.
            final int canConfirmCount = event.getParticipantLimit() - participantCount;

            confirmed = requestList.stream().limit(canConfirmCount).collect(toUnmodifiableList());
            rejected = requestList.stream().skip(canConfirmCount).collect(toUnmodifiableList());

            setRequestsStatus(confirmed, RequestStatus.CONFIRMED);
            setRequestsStatus(rejected, RequestStatus.REJECTED);
        } else if (status.equals(RequestStatus.REJECTED)) {
            // Все отклоняем.
            setRequestsStatus(requestList, RequestStatus.REJECTED);
            rejected = requestList;
            confirmed = Collections.emptyList();
        }

        confirmed = requestDao.saveAll(confirmed);
        rejected = requestDao.saveAll(rejected);

        resultDto.setRejectedRequests(mapRequestsToDto(rejected));
        resultDto.setConfirmedRequests(mapRequestsToDto(confirmed));

        return resultDto;
    }

    @Override
    @Transactional
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
