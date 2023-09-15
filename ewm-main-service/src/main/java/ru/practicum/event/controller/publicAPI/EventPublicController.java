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
import java.util.List;

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
                                                  @RequestParam(defaultValue = "10") Integer size) {
        // Map<String, Object> map;

        // ToDo
        // Придумать как по нормальному сформировать строку логов.
        // если нет параметра, то не добавлять
        final String logStr = String.format("GET /events?text={text}&categories={categories}&paid={paid}&rangeStart={rangeStart}, " +
                "{text} = %s, {categories} = %s", text, categories);
        log.info(logStr);

        return null;
    }

    //информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
    @GetMapping("/{id}")
    public EventFullDto getEventFullInfo(@PathVariable long id, HttpServletRequest request) throws JsonProcessingException {
        log.info(String.format("GET /events/{id}, {id} = %s", id));

        final EventFullDto eventFullDto = eventService.getEvent(id);
        clientWrapper.saveHit(request);

        return eventFullDto;
    }
}
