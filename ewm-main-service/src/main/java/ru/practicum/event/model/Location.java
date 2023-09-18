package ru.practicum.event.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    @JsonProperty("lat")
    private double latitude;
    @JsonProperty("lon")
    private double longitude;
}
