package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.event.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@SuperBuilder
@Jacksonized
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateDto {
    @NotBlank
    private String annotation;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    @JsonProperty("category")
    private Long category_id;
    @NotNull
    private LocalDateTime eventDate;
    private Location location;
    @JsonProperty("paid")
    private boolean isPaid;
    private int participantLimit;
    @JsonProperty("requestModeration")
    private boolean isModerationRequired;
}
