package ru.zipprey.eventify.bot.command;

public interface TelegramCommand {

    String getKey();

    void execute(long chatId, String[] args);
}
