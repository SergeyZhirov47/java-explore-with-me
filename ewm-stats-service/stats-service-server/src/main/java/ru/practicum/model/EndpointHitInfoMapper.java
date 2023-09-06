package ru.practicum.model;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.EndpointHitInfoDto;

import static java.util.Objects.isNull;

@UtilityClass
public class EndpointHitInfoMapper {
    public EndpointHitInfo toEndpointHitInfo(EndpointHitInfoDto infoDto) {
        if (isNull(infoDto)) return null;

        return EndpointHitInfo.builder()
                .app(infoDto.getApp())
                .uri(infoDto.getUri())
                .ip(infoDto.getIp())
                .timestamp(infoDto.getTimestamp())
                .build();
    }
}
