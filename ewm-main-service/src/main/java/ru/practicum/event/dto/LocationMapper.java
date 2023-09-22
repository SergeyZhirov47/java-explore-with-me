package ru.practicum.event.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.event.model.Location;

import static java.util.Objects.isNull;

@UtilityClass
public class LocationMapper {
    public Location toLocation(LocationDto locationDto) {
        if (isNull(locationDto)) {
            return null;
        }

        return Location.builder()
                .latitude(locationDto.getLatitude())
                .longitude(locationDto.getLongitude())
                .build();
    }

    public LocationDto toLocationDto(Location location) {
        if (isNull(location)) {
            return null;
        }

        return LocationDto.builder()
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
    }
}
