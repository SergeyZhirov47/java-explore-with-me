package ru.practicum.event.controller.adminAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventUpdateByAdminDto;
import ru.practicum.event.model.EventState;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
@Slf4j
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> search(@RequestParam(required = false) List<Long> users,
                                     @RequestParam(required = false) List<EventState> states,
                                     @RequestParam(required = false) List<Long> categories,
                                     @RequestParam(required = false) LocalDateTime rangeStart,
                                     @RequestParam(required = false) LocalDateTime rangeEnd,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        // ToDo
        // Придумать как по нормальному сформировать строку логов.
        // если нет параметра, то не добавлять
        final String logStr = String.format("GET /admin/events?users={users}, {users} = %s", users);
        log.info(logStr);

        return eventService.search(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto edit(@PathVariable long eventId, @RequestBody EventUpdateByAdminDto eventUpdateDto) {
        log.info(String.format("PATCH /admin/events/{eventId}, {eventId} = %s, body = %s", eventId, eventUpdateDto));
        return eventService.edit(eventId, eventUpdateDto);
    }
}
