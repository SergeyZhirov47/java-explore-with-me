package ru.practicum.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.EndpointHitInfoDto;
import ru.practicum.dto.EndpointStatsDto;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static ru.practicum.common.Utils.DATE_PARAM_FORMAT_PATTERN;

@Component
@Slf4j
public class StatsClient {
    private static final String STATS_SERVER_BASE_URL = "http://stats-server:9090";
    private final RestTemplate restTemplate;
    private final HttpHeaders headers = new HttpHeaders();
    private final ObjectMapper objectMapper;

    public StatsClient(RestTemplateBuilder builder, ObjectMapper objectMapper) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(STATS_SERVER_BASE_URL))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();

        this.objectMapper = objectMapper;

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    }

    public ResponseEntity<Void> callEndpointHit(String app, String uri, String ip) throws JsonProcessingException {
        val endpointHitInfoDto = EndpointHitInfoDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();

        val json = objectMapper.writeValueAsString(endpointHitInfoDto);
        val requestEntity = new HttpEntity<>(json, headers);

        ResponseEntity<Void> response = null;
        try {
            response = restTemplate.exchange(STATS_SERVER_BASE_URL + "/hit", HttpMethod.POST, requestEntity, Void.class);
        } catch (RestClientException exp) {
            log.warn(exp.getMessage());
        }

        return response;
    }

    public ResponseEntity<List<EndpointStatsDto>> callEndpointStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        final ParameterizedTypeReference<List<EndpointStatsDto>> responseType = new ParameterizedTypeReference<>() {
        };

        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_PARAM_FORMAT_PATTERN);
        final URI uri = UriComponentsBuilder
                .fromHttpUrl(STATS_SERVER_BASE_URL)
                .path("/stats")
                .queryParam("start", start.format(dateTimeFormatter))
                .queryParam("end", end.format(dateTimeFormatter))
                .queryParamIfPresent("uris", Optional.of(uris))
                .queryParamIfPresent("unique", Optional.of(unique))
                .build()
                .toUri();

        ResponseEntity<List<EndpointStatsDto>> response = null;
        try {
            response = restTemplate.exchange(uri, HttpMethod.GET, null, responseType);
        } catch (RestClientException exp) {
            log.warn(exp.getMessage());
        }

        return response;
    }
}
