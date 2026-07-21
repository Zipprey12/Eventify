package ru.zipprey.eventify.notification.exception;

public class TelegramLinkedToAnotherUserException extends RuntimeException {

    public TelegramLinkedToAnotherUserException() {
        super("Телеграм аккаунт уже привязан к другому аккаунту Eventify");
    }
}
