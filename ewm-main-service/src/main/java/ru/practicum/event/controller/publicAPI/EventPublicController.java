package ru.practicum.event.controller.publicAPI;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.StatsClientWrapper;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.EventSort;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
@Slf4j
public class EventPublicController {
    private final EventService eventService;
    private final StatsClientWrapper clientWrapper;

    @GetMapping
    public List<EventShortDto> getPublishedEvents(@RequestParam(required = false) String text,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false) LocalDateTime rangeStart,
                                                  @RequestParam(required = false) LocalDateTime rangeEnd,
                                                  @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                  @RequestParam(required = false) EventSort sort,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  HttpServletRequest request) throws JsonProcessingException {
        Map<String, Object> params = new HashMap<>();
        params.put("text", text);
        params.put("categories", categories);
        params.put("paid", paid);
        params.put("rangeStart", rangeStart);
        params.put("rangeEnd", rangeEnd);
        params.put("onlyAvailable", onlyAvailable);
        params.put("sort", sort);
        params.put("from", from);
        params.put("size", size);

        params = params.entrySet().stream()
                .filter(p -> nonNull(p.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        StringBuilder uriStringBuilder = new StringBuilder("GET /events?");
        StringBuilder paramsStringBuilder = new StringBuilder();
        for (Map.Entry<String, Object> kv : params.entrySet()) {
            String paramNameStr = kv.getKey() + "={" + kv.getKey() + "}";
            String paramValueStr = kv.getKey() + " = " + kv.getValue().toString();

            uriStringBuilder.append(paramNameStr);
            paramsStringBuilder.append(paramValueStr);
        }

        log.info(uriStringBuilder + ", " + paramsStringBuilder);

        final List<EventShortDto> publishedEvents = eventService.getPublishedEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        clientWrapper.saveHit(request);

        return publishedEvents;
    }

    @GetMapping("/{id}")
    public EventFullDto getEventFullInfo(@PathVariable long id, HttpServletRequest request) throws JsonProcessingException {
        log.info(String.format("GET /events/{id}, {id} = %s", id));

        final EventFullDto eventFullDto = eventService.getEvent(id);
        clientWrapper.saveHit(request);

        return eventFullDto;
    }
}
