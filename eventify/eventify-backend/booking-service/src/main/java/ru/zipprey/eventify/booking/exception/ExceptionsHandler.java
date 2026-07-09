package ru.zipprey.eventify.booking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.zipprey.eventify.common.model.ErrorResponse;
import ru.zipprey.eventify.common.model.Level;
import ru.zipprey.eventify.eventapi.exception.EventNotFoundException;
import ru.zipprey.eventify.eventapi.exception.NotEnoughTicketsException;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(BookingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerNotFoundException(BookingNotFoundException e) {
        return new ErrorResponse("BOOKING_NOT_FOUND", Level.ERROR, e.getMessage(), null);
    }

    @ExceptionHandler(BookingAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleBookingAccessDeniedException(BookingAccessDeniedException e) {
        return new ErrorResponse("ACCESS_DENIED", Level.ERROR, e.getMessage(), null);
    }

    @ExceptionHandler(NotEnoughTicketsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleNotEnoughTickets(NotEnoughTicketsException e) {
        return new ErrorResponse("NOT_ENOUGH_TICKETS", Level.ERROR, e.getMessage(), null);
    }

    @ExceptionHandler(BookingExpiredException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleBookingExpired(BookingExpiredException e) {
        return new ErrorResponse("BOOKING_EXPIRED", Level.ERROR, e.getMessage(), null);
    }

    @ExceptionHandler(EventNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEventNotFoundException(EventNotFoundException e) {
        return new ErrorResponse("EVENT_NOT_FOUND", Level.ERROR, e.getMessage(), null);
    }

    @ExceptionHandler(EventServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleEventServiceException(EventServiceUnavailableException e) {
        return new ErrorResponse("EVENT_SERVER_ERROR", Level.ERROR, e.getMessage(), null);
    }
}
