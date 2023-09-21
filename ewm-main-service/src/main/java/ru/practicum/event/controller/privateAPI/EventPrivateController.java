package ru.practicum.event.controller.privateAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventCreateDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventUpdateByUserDto;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateDataDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.common.Utils.DEFAULT_FROM_VALUE;
import static ru.practicum.common.Utils.DEFAULT_SIZE_VALUE;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users/{userId}/events")
@Slf4j
public class EventPrivateController {
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping
    public List<EventFullDto> getUserEvents(@PathVariable long userId,
                                            @PositiveOrZero @RequestParam(defaultValue = DEFAULT_FROM_VALUE) Integer from,
                                            @Positive @RequestParam(defaultValue = DEFAULT_SIZE_VALUE) Integer size) {
        log.info(String.format("GET /users/{userId}/events, {userId} = %s, from = %s, size = %s", userId, from, size));
        return eventService.getEventsByUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEvent(@PathVariable long userId, @PathVariable long eventId) {
        log.info(String.format("GET /users/{userId}/events/{eventId}, {userId} = %s, {eventId} = %s", userId, eventId));
        return eventService.getEventByUser(userId, eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable long userId, @Valid @RequestBody EventCreateDto eventCreateDto) {
        log.info(String.format("POST /users/{userId}/events, {userId} = %s, body = %s", userId, eventCreateDto));
        return eventService.add(userId, eventCreateDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto editEvent(@PathVariable long userId,
                                  @PathVariable long eventId,
                                  @RequestBody EventUpdateByUserDto eventUpdateDto) {
        log.info(String.format("PATCH /users/{userId}/events/{eventId}, {userId} = %s, {eventId} = %s, body = %s", userId, eventId, eventUpdateDto));
        return eventService.edit(userId, eventId, eventUpdateDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequestsInUserEvent(@PathVariable long userId, @PathVariable long eventId) {
        log.info(String.format("GET /users/{userId}/events/{eventId}/requests, {userId} = %s, {eventId} = %s", userId, eventId));
        return requestService.getRequestsInUserEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResultDto changeRequestStatus(@PathVariable long userId,
                                                                 @PathVariable long eventId,
                                                                 @RequestBody EventRequestStatusUpdateDataDto updateDataDto) {
        log.info(String.format("PATCH /users/{userId}/events/{eventId}/requests, {userId} = %s, {eventId} = %s, body = %s", userId, eventId, updateDataDto));
        return requestService.changeStatus(userId, eventId, updateDataDto);
    }
}
