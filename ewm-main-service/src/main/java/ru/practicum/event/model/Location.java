package ru.practicum.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
@AllArgsConstructor
public class Location {
    private double latitude;
    private double longitude;
}
