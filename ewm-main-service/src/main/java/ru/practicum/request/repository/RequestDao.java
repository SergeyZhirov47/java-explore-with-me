package ru.practicum.request.repository;

import org.springframework.data.domain.Sort;
import ru.practicum.request.model.Request;

import java.util.List;
import java.util.Map;

public interface RequestDao {
    Request save(Request request);

    List<Request> saveAll(Iterable<Request> requests);

    Request getRequest(long id);

    List<Request> getRequests(List<Long> ids);

    List<Request> getRequests(List<Long> ids, Sort sort);

    List<Request> getUserRequests(long userId);

    List<Request> getUserRequestsInEvent(long userId, long eventId);

    void checkRequestExists(long id);

    int getParticipantCountInEvent(long eventId);

    int getConfirmedRequestsCount(long eventId);

    // Ключ - id события, значение - кол-во подтвержденных заявок на это событие.
    Map<Long, Long> getConfirmedRequestsCountMap(List<Long> eventIds);
}