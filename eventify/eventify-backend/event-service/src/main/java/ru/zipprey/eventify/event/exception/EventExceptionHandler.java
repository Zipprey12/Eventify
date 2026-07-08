package ru.zipprey.eventify.event.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.zipprey.eventify.common.model.ErrorResponse;
import ru.zipprey.eventify.common.model.Level;

@RestControllerAdvice
public class EventExceptionHandler {

    @ExceptionHandler(EventNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEventNotFoundException(EventNotFoundException e) {
        return new ErrorResponse("EVENT_NOT_FOUND", Level.ERROR, e.getMessage(), null);
    }

    @ExceptionHandler(CapacityReductionException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleCapacityReduction(CapacityReductionException e) {
        return new ErrorResponse("CAPACITY_REDUCTION_NOT_ALLOWED", Level.ERROR, e.getMessage(), null);
    }
}
