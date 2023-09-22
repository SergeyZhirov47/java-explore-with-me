package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserDto;

import java.time.LocalDateTime;

import static ru.practicum.common.Utils.DATE_PARAM_FORMAT_PATTERN;

@Builder
@Jacksonized
@Data
@AllArgsConstructor
public class EventShortDto {
    private long id;
    private String annotation;
    private String title;
    private String description;
    private CategoryDto category;
    private UserDto initiator;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PARAM_FORMAT_PATTERN)
    private LocalDateTime eventDate;
    @JsonProperty("paid")
    private Boolean isPaid;
    private long confirmedRequests;
    private long views;
}
