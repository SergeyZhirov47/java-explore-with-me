package ru.practicum.event.controller.adminAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventUpdateByAdminDto;
import ru.practicum.event.model.EventState;
import ru.practicum.event.service.EventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static ru.practicum.common.Utils.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
@Validated
@Slf4j
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> search(@RequestParam(required = false) List<Long> users,
                                     @RequestParam(required = false) List<EventState> states,
                                     @RequestParam(required = false) List<Long> categories,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = DATE_PARAM_FORMAT_PATTERN) LocalDateTime rangeStart,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = DATE_PARAM_FORMAT_PATTERN) LocalDateTime rangeEnd,
                                     @PositiveOrZero @RequestParam(defaultValue = DEFAULT_FROM_VALUE) Integer from,
                                     @Positive @RequestParam(defaultValue = DEFAULT_SIZE_VALUE) Integer size) {
        Map<String, Object> params = new HashMap<>();
        params.put("users", users);
        params.put("states", states);
        params.put("categories", categories);
        params.put("rangeStart", rangeStart);
        params.put("rangeEnd", rangeEnd);
        params.put("from", from);
        params.put("size", size);

        params = params.entrySet().stream()
                .filter(p -> nonNull(p.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        final List<String> paramValueList = new ArrayList<>();
        for (Map.Entry<String, Object> kv : params.entrySet()) {
            String paramValueStr = kv.getKey() + " = " + kv.getValue().toString();
            paramValueList.add(paramValueStr);
        }

        log.info("GET /admin/events c параметрами: " + String.join(", ", paramValueList));

        return eventService.search(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto edit(@PathVariable long eventId, @RequestBody EventUpdateByAdminDto eventUpdateDto) {
        log.info(String.format("PATCH /admin/events/{eventId}, {eventId} = %s, body = %s", eventId, eventUpdateDto));
        return eventService.edit(eventId, eventUpdateDto);
    }
}
