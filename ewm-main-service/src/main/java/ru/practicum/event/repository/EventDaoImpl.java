package ru.practicum.event.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.stereotype.Repository;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventSort;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.QEvent;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Repository
public class EventDaoImpl implements EventDao {
    private final EventRepository eventRepository;
    private final JPAQueryFactory queryFactory;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public EventDaoImpl(EntityManager entityManager, EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }

    @Override
    public Event save(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public void checkEventExists(long id) {
        if (!eventRepository.existsById(id)) {
            throw new NotFoundException(String.format("Событие с id = %s не найдено!", id));
        }
    }

    @Override
    public Event getEvent(long id) {
        final Optional<Event> eventOptional = eventRepository.findById(id);
        return eventOptional.orElseThrow(() -> new NotFoundException(String.format("Событие с id = %s не найдено!", id)));
    }

    @Override
    public List<Event> getEvents(List<Long> ids) {
        return eventRepository.findAllByIdIn(ids);
    }

    @Override
    public Event getEventByUser(long id, long userId) {
        final Optional<Event> eventOptional = eventRepository.findByIdAndInitiatorId(id, userId);
        return eventOptional.orElseThrow(() -> new NotFoundException(String.format("Не найдено событие с id = %s, которое разместил пользователь с id = %s", id, userId)));
    }

    @Override
    public List<Event> getEventsByUser(long userId, Pageable pageable) {
        return eventRepository.findAllByInitiatorId(userId, pageable);
    }

    @Override
    public List<Event> searchEvents(List<Long> userIds,
                                    List<EventState> states,
                                    List<Long> categoryIds,
                                    LocalDateTime start,
                                    LocalDateTime end,
                                    Pageable pageable) {
        final QEvent qEvent = QEvent.event;

        BooleanExpression whereExpression = qEvent.initiator().id.in(userIds);
        whereExpression = whereExpression.and(qEvent.state.in(states));
        whereExpression = whereExpression.and(qEvent.category().id.in(categoryIds));
        whereExpression = whereExpression.and(qEvent.eventDate.after(start));
        whereExpression = whereExpression.and(qEvent.eventDate.before(end));

        final JPAQuery<Event> searchQuery = queryFactory.selectFrom(qEvent).where(whereExpression);

        final List<Event> result;
        if (nonNull(pageable) && pageable.isPaged()) {
            final Querydsl querydsl = new Querydsl(entityManager, (new PathBuilderFactory()).create(QEvent.class));
            result = querydsl.applyPagination(pageable, searchQuery).fetch();
        } else {
            result = searchQuery.fetch();
        }

        return result;
    }

    @Override
    public List<Event> getPublishedEvents(String text,
                                          List<Long> categoryIds,
                                          Boolean paid,
                                          LocalDateTime start,
                                          LocalDateTime end,
                                          EventSort sort,
                                          Pageable pageable) {
        final QEvent qEvent = QEvent.event;

        BooleanExpression whereExpression = qEvent.state.eq(EventState.PUBLISHED);
        whereExpression = whereExpression.and(qEvent.annotation.containsIgnoreCase(text)
                .or(qEvent.description.containsIgnoreCase(text)));
        whereExpression = whereExpression.and(qEvent.category().id.in(categoryIds));
        whereExpression = whereExpression.and(qEvent.isPaid.eq(paid));
        whereExpression = whereExpression.and(qEvent.eventDate.after(start));
        whereExpression = whereExpression.and(qEvent.eventDate.before(end));

        JPAQuery<Event> searchQuery = queryFactory.selectFrom(qEvent)
                .where(whereExpression);

        if (nonNull(sort) && sort.equals(EventSort.EVENT_DATE)) {
            searchQuery = searchQuery.orderBy(qEvent.eventDate.asc());
        }

        final List<Event> result;
        if (nonNull(pageable) && pageable.isPaged()) {
            final Querydsl querydsl = new Querydsl(entityManager, (new PathBuilderFactory()).create(QEvent.class));
            result = querydsl.applyPagination(pageable, searchQuery).fetch();
        } else {
            result = searchQuery.fetch();
        }

        return result;
    }
}
