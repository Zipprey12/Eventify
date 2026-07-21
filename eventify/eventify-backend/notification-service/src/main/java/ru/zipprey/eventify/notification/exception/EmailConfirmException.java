package ru.zipprey.eventify.notification.exception;

public class EmailConfirmException extends RuntimeException {

    public EmailConfirmException() {
        super("Ошибка подтверждения email");
    }

}
