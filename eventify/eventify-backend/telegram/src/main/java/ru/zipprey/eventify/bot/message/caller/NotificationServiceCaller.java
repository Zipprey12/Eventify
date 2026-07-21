package ru.zipprey.eventify.bot.message.caller;

public interface NotificationServiceCaller {

    void unlink(long chatId);

    String link(long chatId, String code);
}
