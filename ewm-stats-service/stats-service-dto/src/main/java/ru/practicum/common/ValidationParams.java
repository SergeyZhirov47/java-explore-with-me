package ru.practicum.common;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;

@UtilityClass
public class ValidationParams {
    public void validateStartEndDate(LocalDateTime start, LocalDateTime end) {
        if (isNull(start) || isNull(end)) {
            return;
        }

        if (start.isAfter(end)) {
            throw new IllegalArgumentException(String.format("start - %s не может быть позже end - %s!", start, end));
        }
    }
}
