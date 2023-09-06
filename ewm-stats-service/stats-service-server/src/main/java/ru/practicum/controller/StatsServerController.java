package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitInfoDto;
import ru.practicum.dto.EndpointStatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsServerController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@RequestBody EndpointHitInfoDto endpointHitInfoDto) {
        log.info(String.format("POST /hit, body = %s", endpointHitInfoDto));
        statsService.saveHitInfo(endpointHitInfoDto);
    }

    @GetMapping("/stats")
    public List<EndpointStatsDto> stats(@RequestParam LocalDateTime start,
                                        @RequestParam LocalDateTime end,
                                        @RequestParam(defaultValue = "") List<String> uris,
                                        @RequestParam(defaultValue = "false") Boolean unique) {
        log.info(String.format("GET /stats, start = %s, end = %s, uris = %s, unique = %s", start, end, uris, unique));
        return statsService.getStatistics(start, end, uris, unique);
    }
}
