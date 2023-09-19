package ru.practicum.common;

import static java.util.Objects.isNull;

public class Utils {
    public static final String DATE_PARAM_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_FROM_VALUE = "0";
    public static final String DEFAULT_SIZE_VALUE = "10";

    public static void validateLengthOfNullableString(String str, int min, int max) {
        if (isNull(str)) {
            return;
        }

        final int length = str.length();
        if (length < min) {
            throw new IllegalArgumentException("less then min");
        }
        if (length > max) {
            throw new IllegalArgumentException("greater then max");
        }
    }
}
