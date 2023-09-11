package ru.practicum.unit;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.EndpointHitInfoDto;
import ru.practicum.model.EndpointHitInfo;
import ru.practicum.repository.DaoEndpointHitInfo;
import ru.practicum.service.StatsServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StatsServiceTest {
    @Mock
    private DaoEndpointHitInfo daoEndpointHitInfo;
    @InjectMocks
    private StatsServiceImpl statsService;

    @Test
    public void saveHitInfo_whenOk_thenSave() {
        val endpointHitInfoDto = EndpointHitInfoDto.builder()
                .app("app")
                .uri("/someUri")
                .ip("127.0.0.1")
                .timestamp(LocalDateTime.now())
                .build();

        Mockito.when(daoEndpointHitInfo.save(any(EndpointHitInfo.class))).thenReturn(any(EndpointHitInfo.class));

        statsService.saveHitInfo(endpointHitInfoDto);

        verify(daoEndpointHitInfo).save(any(EndpointHitInfo.class));
    }

    @Test
    public void getStatistics_whenHasStartAndEnd_thenReturnStats() {
        val start = LocalDateTime.now();
        val end = LocalDateTime.now().plusDays(1);

        Mockito.when(daoEndpointHitInfo.getStatistics(start, end, null, null)).thenReturn(Collections.emptyList());

        val stats = statsService.getStatistics(start, end, null, null);

        verify(daoEndpointHitInfo).getStatistics(start, end, null, null);
    }

    @Test
    public void getStatistics_whenEndBeforeStart_thenThrowException() {
        val start = LocalDateTime.now();
        val end = LocalDateTime.now().minusDays(1);

        assertThrows(IllegalArgumentException.class, () -> statsService.getStatistics(start, end, null, null));

        verify(daoEndpointHitInfo, never()).getStatistics(start, end, null, null);
    }

    @Test
    public void getStatistics_whenHasAllParams_thenReturnStats() {
        val start = LocalDateTime.now();
        val end = LocalDateTime.now().plusDays(1);
        val uris = List.of("uri1/1", "uri2/1");
        val unique = Boolean.TRUE;

        Mockito.when(daoEndpointHitInfo.getStatistics(start, end, uris, unique)).thenReturn(Collections.emptyList());

        val stats = statsService.getStatistics(start, end, uris, unique);

        verify(daoEndpointHitInfo).getStatistics(start, end, uris, unique);
    }
}
