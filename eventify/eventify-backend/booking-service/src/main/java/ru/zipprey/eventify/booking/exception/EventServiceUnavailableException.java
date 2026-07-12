package ru.zipprey.eventify.booking.exception;

public class EventServiceUnavailableException extends RuntimeException {

    public EventServiceUnavailableException(Long eventId) {
        super("Не удалось получить данные события с id: " + eventId);
    }

    public EventServiceUnavailableException(Long eventId, Throwable cause) {
        super("Не удалось получить данные события с id: " + eventId, cause);
    }

}
