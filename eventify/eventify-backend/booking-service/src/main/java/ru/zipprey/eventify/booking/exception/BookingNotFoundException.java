package ru.zipprey.eventify.booking.exception;

public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(long id) {
        super("Бронь " + id + " не найдена");
    }
}
