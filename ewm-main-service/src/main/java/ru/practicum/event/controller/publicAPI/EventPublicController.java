package ru.practicum.event.controller.publicAPI;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.StatsClientWrapper;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.EventSort;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.common.Utils.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/events")
@Slf4j
public class EventPublicController {
    private final EventService eventService;
    private final StatsClientWrapper clientWrapper;

    @GetMapping
    public List<EventShortDto> getPublishedEvents(@RequestParam(required = false) String text,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = DATE_PARAM_FORMAT_PATTERN) LocalDateTime rangeStart,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = DATE_PARAM_FORMAT_PATTERN) LocalDateTime rangeEnd,
                                                  @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                  @RequestParam(required = false) EventSort sort,
                                                  @PositiveOrZero @RequestParam(defaultValue = DEFAULT_FROM_VALUE) Integer from,
                                                  @Positive @RequestParam(defaultValue = DEFAULT_SIZE_VALUE) Integer size,
                                                  HttpServletRequest request) throws JsonProcessingException {
        log.info("GET /events c параметрами: text = {}, categories = {}, paid = {}, rangeStart = {}, rangeEnd = {}, onlyAvailable = {}, sort = {}, from = {}, size = {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        final List<EventShortDto> publishedEvents = eventService.getPublishedEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        clientWrapper.saveHit(request);

        return publishedEvents;
    }

    @GetMapping("/{id}")
    public EventFullDto getEventFullInfo(@PathVariable long id, HttpServletRequest request) throws JsonProcessingException {
        log.info(String.format("GET /events/{id}, {id} = %s", id));

        final EventFullDto eventFullDto = eventService.getEventOnlyIfPublished(id);
        clientWrapper.saveHit(request);

        return eventFullDto;
    }
}
