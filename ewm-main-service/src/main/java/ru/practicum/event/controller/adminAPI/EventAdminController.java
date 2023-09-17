package ru.practicum.event.controller.adminAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventUpdateByAdminDto;
import ru.practicum.event.model.EventState;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

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
//        Map<String, Object> params = new HashMap<>();
//        params.put("users", users);
//        params.put("states", states);
//        params.put("categories", categories);
//        params.put("rangeStart", rangeStart);
//        params.put("rangeEnd", rangeEnd);
//        params.put("from", from);
//        params.put("size", size);

//        params = params.entrySet().stream()
//                .filter(p -> nonNull(p.getValue()))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//
//        StringBuilder uriStringBuilder = new StringBuilder("GET /admin/events?");
//        StringBuilder paramsStringBuilder = new StringBuilder();
//        for (Map.Entry<String, Object> kv : params.entrySet()) {
//            String paramNameStr = kv.getKey() + "={" + kv.getKey() + "}";
//            String paramValueStr = kv.getKey() + " = " + kv.getValue().toString();
//
//            uriStringBuilder.append(paramNameStr);
//            paramsStringBuilder.append(paramValueStr);
//        }

        //final String logStr = String.format("GET %s, %s", uriStringBuilder.toString(), paramsStringBuilder.toString());
       // log.info(uriStringBuilder + ", " + paramsStringBuilder);
        log.info("GET /admin/events");

        return eventService.search(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto edit(@PathVariable long eventId, @RequestBody EventUpdateByAdminDto eventUpdateDto) {
        log.info(String.format("PATCH /admin/events/{eventId}, {eventId} = %s, body = %s", eventId, eventUpdateDto));
        return eventService.edit(eventId, eventUpdateDto);
    }
}
