package ru.zipprey.eventify.bot.command.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.bot.command.AsyncCommand;
import ru.zipprey.eventify.bot.service.sender.MessageSender;

import java.util.concurrent.Executor;

@Component
public class HelpCommand extends AsyncCommand {

    public static final String KEY = "/help";

    public static final String TEXT = """
            Список доступных команд:
            /my_books - получить список своих бронирований
            /notification_time - установить время напоминания о бронировании
            /stop - перестать получать уведомления
            """;

    private final MessageSender sender;

    public HelpCommand(@Qualifier("botTaskExecutor") Executor executor, MessageSender sender) {
        super(executor, sender);
        this.sender = sender;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    protected void handle(long chatId, String[] args) {
        sender.sendText(chatId, TEXT);
    }
}
