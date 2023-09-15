package ru.practicum.event.service;

import ru.practicum.event.dto.EventCreateDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventUpdateByAdminDto;
import ru.practicum.event.dto.EventUpdateByUserDto;
import ru.practicum.event.model.EventSort;
import ru.practicum.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto add(long userId, EventCreateDto eventCreateDto);

    EventFullDto edit(long userId, long eventId, EventUpdateByUserDto eventCreateDto);

    EventFullDto edit(long eventId, EventUpdateByAdminDto eventCreateDto);

    EventFullDto getEventByUser(long userId, long eventId);

    EventFullDto getEvent(long id);

    List<EventFullDto> search(List<Long> userIds,
                              List<EventState> states,
                              List<Long> categoryIds,
                              LocalDateTime start,
                              LocalDateTime end,
                              Integer from,
                              Integer size);

    List<EventFullDto> getPublishedEvents(String text,
                                          List<Long> categoryIds,
                                          Boolean paid,
                                          LocalDateTime start,
                                          LocalDateTime end,
                                          Boolean onlyAvailable,
                                          EventSort sort,
                                          Integer from,
                                          Integer size);

    List<EventFullDto> getEventsByUser(long userId, Integer from, Integer size);
}
