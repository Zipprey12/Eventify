package ru.zipprey.eventify.eventapi.exception;

public class NotEnoughTicketsException extends RuntimeException{

    public NotEnoughTicketsException(String event, int freeCount){
        super("На \"" + event + "\" доступно билетов: " + freeCount);
    }

}
