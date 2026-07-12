package ru.zipprey.eventify.booking.exception;

public class BookingExpiredException extends RuntimeException {

    public BookingExpiredException(long id) {
        super("Срок заявки на бронирование " + id + " истек");
    }

}
