package ru.zipprey.eventify.notification.exception;

public class TelegramInvalidCodeException extends RuntimeException {

    public TelegramInvalidCodeException() {
        super("Код недействителен или устарел");
    }

}
