package ru.zipprey.eventify.bot.exception;

public class TelegramInvalidCodeException extends RuntimeException {

    public TelegramInvalidCodeException() {
        super("Код недействителен или устарел");
    }

}