package ru.practicum.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.common.exception.NotFoundException;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundException.class, IllegalArgumentException.class})
    public ErrorResponseData handleForNotFound(RuntimeException exp) {
        log.warn(exp.getMessage(), exp);
        return new ErrorResponseData(exp.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({IllegalStateException.class})
    public ErrorResponseData handleForConflict(RuntimeException exp) {
        log.warn(exp.getMessage(), exp);
        return new ErrorResponseData(exp.getMessage());
    }
}
