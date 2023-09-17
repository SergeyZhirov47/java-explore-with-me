package ru.practicum.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class StatsClientWrapper {
    private final StatsClient statsClient;

    public void saveHit(HttpServletRequest request) throws JsonProcessingException {
        // ToDo
        // Вынести название приложения в константу
        statsClient.callEndpointHit("app", request.getRequestURI(), request.getRemoteAddr());
    }

    public List<EndpointStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (isNull(start)) {
            start = LocalDateTime.of(1900, 1, 1, 0, 0, 0);
        }
        if (isNull(end)) {
            end = LocalDateTime.of(3000, 12, 31, 23, 59, 59);
        }

        final ResponseEntity<List<EndpointStatsDto>> response = statsClient.callEndpointStats(start, end, uris, unique);
        return response.getBody();
    }
}
