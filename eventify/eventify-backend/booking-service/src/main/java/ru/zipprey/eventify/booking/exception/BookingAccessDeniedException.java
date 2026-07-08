package ru.zipprey.eventify.booking.exception;

public class BookingAccessDeniedException extends RuntimeException {

    public BookingAccessDeniedException(long id) {
        super("Доступа к брони " + id + " запрещен");
    }
}
