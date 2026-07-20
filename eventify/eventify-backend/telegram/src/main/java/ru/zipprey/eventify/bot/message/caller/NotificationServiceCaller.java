package ru.zipprey.eventify.bot.message.caller;

import java.util.Optional;

public interface NotificationServiceCaller {

    void unlink(long chatId);

    Optional<String> link(long chatId, String code);
}
