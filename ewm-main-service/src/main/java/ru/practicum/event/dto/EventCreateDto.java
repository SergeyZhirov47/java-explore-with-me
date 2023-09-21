package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.common.Utils.DATE_PARAM_FORMAT_PATTERN;

@SuperBuilder
@Jacksonized
@Data
public class EventCreateDto {
    @NotBlank
    @Length(min = 20, max = 2000)
    private String annotation;
    @NotBlank
    @Length(min = 3, max = 120)
    private String title;
    @NotBlank
    @Length(min = 20, max = 7000)
    private String description;
    @NotNull
    @JsonProperty("category")
    private Long categoryId;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PARAM_FORMAT_PATTERN)
    private LocalDateTime eventDate;
    @NotNull
    @Valid
    private LocationDto location;
    @JsonProperty("paid")
    private Boolean isPaid;
    private Integer participantLimit;
    @JsonProperty("requestModeration")
    private Boolean isModerationRequired;
}
