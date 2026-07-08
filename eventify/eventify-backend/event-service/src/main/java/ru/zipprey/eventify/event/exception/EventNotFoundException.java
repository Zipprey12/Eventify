package ru.zipprey.eventify.event.exception;

public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException(long id) {
        super("Событие " + id + " не найдено");
    }

}
