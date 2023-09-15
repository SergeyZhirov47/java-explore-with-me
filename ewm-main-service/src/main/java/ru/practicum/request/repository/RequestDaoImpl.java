package ru.practicum.request.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.request.model.QRequest;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RequestDaoImpl implements RequestDao {
    private final RequestRepository requestRepository;
    private final JPAQueryFactory queryFactory;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public RequestDaoImpl(EntityManager entityManager, RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
        queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }

    @Override
    public Request save(Request request) {
        return requestRepository.save(request);
    }

    @Override
    public List<Request> saveAll(Iterable<Request> requests) {
        return requestRepository.saveAll(requests);
    }

    @Override
    public Request getRequest(long id) {
        final Optional<Request> optionalRequest = requestRepository.findById(id);
        return optionalRequest.orElseThrow(() -> new NotFoundException(String.format("Заявка с id = %s не найдена!", id)));
    }

    @Override
    public List<Request> getRequests(List<Long> ids) {
        return requestRepository.findAllByIdIn(ids);
    }

    @Override
    public List<Request> getRequests(List<Long> ids, Sort sort) {
        return requestRepository.findAllByIdIn(ids, sort);
    }

    @Override
    public List<Request> getUserRequests(long userId) {
        return requestRepository.findAllByRequesterId(userId);
    }

    @Override
    public List<Request> getUserRequestsInEvent(long userId, long eventId) {
        return requestRepository.findAllByRequesterIdAndEventId(userId, eventId);
    }

    @Override
    public void checkRequestExists(long id) {
        if (!requestRepository.existsById(id)) {
            throw new NotFoundException(String.format("Заявка с id = %s не найдена!", id));
        }
    }

    @Override
    public int getParticipantCountInEvent(long eventId) {
        return requestRepository.countAllByEventIdAndStatusIn(eventId, List.of(RequestStatus.PENDING, RequestStatus.CONFIRMED));
    }

    @Override
    public int getConfirmedRequestsCount(long eventId) {
        return requestRepository.countAllByEventIdAndStatusIn(eventId, List.of(RequestStatus.CONFIRMED));
    }

    @Override
    public Map<Long, Long> getConfirmedRequestsCount(List<Long> eventIds) {
        final QRequest qRequest = QRequest.request;

        BooleanExpression whereExpression = qRequest.event().id.in(eventIds);
        whereExpression = whereExpression.and(qRequest.status.eq(RequestStatus.CONFIRMED));

        final SimpleExpression<Long> requestId = qRequest.id.count();
        final NumberPath<Long> aliasConfirmedCount = Expressions.numberPath(Long.class, "confirmedCount");

        final List<Tuple> queryResult = queryFactory.selectFrom(qRequest)
                .where(whereExpression)
                .groupBy(qRequest.event().id)
                .select(qRequest.event().id, requestId.as(aliasConfirmedCount))
                .fetch();

        final Map<Long, Long> resultMap = new HashMap<>();
        for (Tuple row : queryResult) {
            resultMap.put(row.get(qRequest.event().id), row.get(aliasConfirmedCount));
        }

//        final List<Request> confirmedRequests = StreamSupport.stream(requestRepository.findAll(whereExpression).spliterator(),false)
//
//                .collect(Collectors.toList());

        return resultMap;
    }
}
