package ru.zipprey.eventify.bot.service.sender;

public interface MessageSender {
    void sendText(long chatId, String text);
}
