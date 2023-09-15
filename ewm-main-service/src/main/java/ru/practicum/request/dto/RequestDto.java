package ru.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.request.model.RequestStatus;

import java.time.LocalDateTime;

import static ru.practicum.common.Utils.DATE_PARAM_FORMAT_PATTERN;

@Builder
@Jacksonized
@Data
@AllArgsConstructor
public class RequestDto {
    private long id;
    @JsonProperty("requester")
    private long requesterId;
    @JsonProperty("event")
    private long eventId;
    private RequestStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PARAM_FORMAT_PATTERN)
    private LocalDateTime created;
}
