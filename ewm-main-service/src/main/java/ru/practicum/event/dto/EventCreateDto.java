package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.Length;
import ru.practicum.event.model.Location;

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
    private Location location;
    @JsonProperty("paid")
    @JsonSetter(nulls = Nulls.SKIP)
    private Boolean isPaid;
    @JsonSetter(nulls = Nulls.SKIP)
    private Integer participantLimit;
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty("requestModeration")
    private Boolean isModerationRequired;

    public EventCreateDto() {
        isPaid = false;
        participantLimit = 0;
        isModerationRequired = true;
    }
}
