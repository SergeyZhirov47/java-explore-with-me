package ru.practicum.common;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.common.exception.NotFoundException;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponseData handleNotFoundReason(NotFoundException exp) {
        log.warn(exp.getMessage(), exp);
        return new ErrorResponseData(exp.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ErrorResponseData handleBadParamsReason(RuntimeException exp) {
        log.warn(exp.getMessage(), exp);
        return new ErrorResponseData(exp.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({IllegalStateException.class,
            DataIntegrityViolationException.class,
            ConstraintViolationException.class})
    public ErrorResponseData handleConflictReason(RuntimeException exp) {
        log.warn(exp.getMessage(), exp);
        return new ErrorResponseData(exp.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseData handleThrowable(Throwable exp) {
        log.warn(exp.getMessage(), exp);
        return new ErrorResponseData(exp.getMessage());
    }
}
