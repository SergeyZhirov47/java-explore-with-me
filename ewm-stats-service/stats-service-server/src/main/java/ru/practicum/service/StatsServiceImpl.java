package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.ValidationParams;
import ru.practicum.dto.EndpointHitInfoDto;
import ru.practicum.dto.EndpointStatsDto;
import ru.practicum.model.EndpointHitInfo;
import ru.practicum.model.EndpointHitInfoMapper;
import ru.practicum.repository.DaoEndpointHitInfo;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final DaoEndpointHitInfo daoEndpointHitInfo;

    @Transactional
    @Override
    public void saveHitInfo(EndpointHitInfoDto endpointHitInfoDto) {
        final EndpointHitInfo endpointHitInfo = EndpointHitInfoMapper.toEndpointHitInfo(endpointHitInfoDto);
        daoEndpointHitInfo.save(endpointHitInfo);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EndpointStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        ValidationParams.validateStartEndDate(start, end);
        return daoEndpointHitInfo.getStatistics(start, end, uris, unique);
    }
}
