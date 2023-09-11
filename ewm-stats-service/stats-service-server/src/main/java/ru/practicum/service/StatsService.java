package ru.practicum.service;


import ru.practicum.dto.EndpointHitInfoDto;
import ru.practicum.dto.EndpointStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void saveHitInfo(EndpointHitInfoDto endpointHitInfoDto);

    List<EndpointStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
