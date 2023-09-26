package ru.practicum.common;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class OffsetPageableValidator {
    public static Pageable validateAndGet(Integer from, Integer size) {
        return validateAndGet(from, size, Sort.unsorted());
    }

    public static Pageable validateAndGet(Integer from, Integer size, Sort sort) {
        if (nonNull(from) && nonNull(size)) {
            if (from < 0) {
                throw new IllegalArgumentException("from - должен быть быть больше или равен нулю!");
            }

            if (size <= 0) {
                throw new IllegalArgumentException("size - должен быть больше нуля!");
            }

            if (isNull(sort)) {
                sort = Sort.unsorted();
            }

            return new OffsetBasedPageRequest(from, size, sort);
        }

        return Pageable.unpaged();
    }
}
