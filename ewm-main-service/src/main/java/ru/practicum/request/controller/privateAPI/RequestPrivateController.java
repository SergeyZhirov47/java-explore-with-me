package ru.practicum.request.controller.privateAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestPrivateController {
    private final RequestService requestService;

    @GetMapping
    public List<RequestDto> getUserRequests(@PathVariable long userId) {
        log.info(String.format("GET /users/{userId}/requests, {userId} = %s", userId));
        return requestService.getUserRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto add(@PathVariable long userId, @RequestParam long eventId) {
        log.info(String.format("POST /users/{userId}/requests/, {userId} = %s, {eventId} = %s", userId, eventId));
        return requestService.add(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancel(@PathVariable long userId, @PathVariable long requestId) {
        log.info(String.format("PATCH /users/{userId}/requests/{requestId}/cancel, {userId} = %s, {requestId} = %s", userId, requestId));
        return requestService.cancel(userId, requestId);
    }
}
