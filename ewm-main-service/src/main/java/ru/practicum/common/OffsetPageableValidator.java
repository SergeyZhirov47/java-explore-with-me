package ru.practicum.common;

import org.springframework.data.domain.Pageable;

import static java.util.Objects.nonNull;

public class OffsetPageableValidator {
    public static Pageable validateAndGet(Integer from, Integer size) {
        if (nonNull(from) && nonNull(size)) {
            if (from < 0) {
                throw new IllegalArgumentException("from - должен быть быть больше или равен нулю!");
            }

            if (size <= 0) {
                throw new IllegalArgumentException("size - должен быть больше нуля!");
            }

            return new OffsetBasedPageRequest(from, size);
        }

        return Pageable.unpaged();
    }
}
