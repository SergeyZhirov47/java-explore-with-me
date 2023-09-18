package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.Length;
import ru.practicum.event.model.Location;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

import static ru.practicum.common.Utils.DATE_PARAM_FORMAT_PATTERN;

@SuperBuilder
@Jacksonized
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateDto {
    @NotBlank
    @Length(min = 20, max = 2000)
    private String annotation;
    @NotBlank
    @Length(min = 3, max = 120)
    private String title;
    @NotBlank
    @Length(min = 20, max = 7000)
    private String description;
    @JsonProperty("category")
    private Long categoryId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PARAM_FORMAT_PATTERN)
    private LocalDateTime eventDate;
    private Location location;
    @JsonProperty("paid")
    private Boolean isPaid;
    private Integer participantLimit;
    @JsonProperty("requestModeration")
    private Boolean isModerationRequired;
}
