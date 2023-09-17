package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventSort;
import ru.practicum.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventDao {
    Event save(Event event);

    void checkEventExists(long id);

    Event getEvent(long id);

    List<Event> getEvents(List<Long> ids);

    Event getEventByUser(long id, long userId);

    List<Event> getEventsByUser(long userId, Pageable pageable);

    List<Event> searchEvents(List<Long> userIds,
                             List<EventState> states,
                             List<Long> categoryIds,
                             LocalDateTime start,
                             LocalDateTime end,
                             Pageable pageable);

    List<Event> getPublishedEvents(String text,
                                   List<Long> categoryIds,
                                   Boolean paid,
                                   LocalDateTime start,
                                   LocalDateTime end,
                                   EventSort sort,
                                   Pageable pageable);
}
