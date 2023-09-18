package ru.practicum.request.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long>, QuerydslPredicateExecutor<Request> {
    List<Request> findAllByRequesterId(long requesterId);

    List<Request> findAllByEventId(long eventId);

    List<Request> findAllByIdIn(List<Long> ids);

    List<Request> findAllByIdIn(List<Long> ids, Sort sort);

    Integer countAllByEventIdAndStatusIn(long eventId, List<RequestStatus> statuses);

    Integer countAllByEventId(long eventId);

    @Query("SELECT case when count(r)> 0 then true else false end FROM Request as r WHERE r.requester.id = :userId AND r.event.id = :eventId")
    boolean hasRequest(@Param("eventId") long eventId, @Param("userId") long userId);
}
