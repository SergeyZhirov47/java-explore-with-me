package ru.practicum.repository;

import ru.practicum.dto.EndpointStatsDto;
import ru.practicum.model.EndpointHitInfo;

import java.time.LocalDateTime;
import java.util.List;

public interface DaoEndpointHitInfo {
    EndpointHitInfo save(EndpointHitInfo endpointHitInfo);

    List<EndpointStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean uniqueIP);
}
