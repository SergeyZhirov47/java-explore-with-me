package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.common.Utils.DATE_PARAM_FORMAT_PATTERN;

@Builder
@Data
@Jacksonized
@AllArgsConstructor
public class EndpointHitInfoDto {
    @NotNull
    private String app;
    @NotNull
    private String uri;
    @NotNull
    private String ip;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PARAM_FORMAT_PATTERN)
    private LocalDateTime timestamp;
}
