package ru.zipprey.eventify.notification.exception;

public class TelegramLinkNotFoundException extends RuntimeException {

    public TelegramLinkNotFoundException(long chatId) {
        super("Привязка Telegram для chatId " + chatId + " не найдена");
    }
}
