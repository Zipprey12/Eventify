package ru.zipprey.eventify.bot.exception;

public class TelegramLinkedToAnotherUserException extends RuntimeException {

    public TelegramLinkedToAnotherUserException() {
        super("Телеграм аккаунт уже привязан к другому аккаунту Eventify");
    }
}
