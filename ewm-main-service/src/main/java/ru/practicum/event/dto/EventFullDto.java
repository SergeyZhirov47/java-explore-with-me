package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserDto;

import java.time.LocalDateTime;

import static ru.practicum.common.Utils.DATE_PARAM_FORMAT_PATTERN;

@Builder
@Jacksonized
@Data
@AllArgsConstructor
public class EventFullDto {
    private long id;
    private String annotation;
    private String title;
    private String description;
    private CategoryDto category;
    private UserDto initiator;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PARAM_FORMAT_PATTERN)
    private LocalDateTime eventDate;
    private Location location;
    @JsonProperty("paid")
    private Boolean isPaid;
    private Integer participantLimit;
    @JsonProperty("requestModeration")
    private Boolean isModerationRequired;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PARAM_FORMAT_PATTERN)
    private LocalDateTime createdOn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PARAM_FORMAT_PATTERN)
    private LocalDateTime publishedOn;
    private EventState state;
    private long confirmedRequests;
    private long views;
}
