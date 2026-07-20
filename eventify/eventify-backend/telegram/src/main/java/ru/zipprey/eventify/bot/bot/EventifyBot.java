package ru.zipprey.eventify.bot.bot;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.zipprey.eventify.bot.command.CommandExecutor;
import ru.zipprey.eventify.bot.command.impl.HelpCommand;
import ru.zipprey.eventify.bot.service.sender.MessageSender;

@Slf4j
@Component
public class EventifyBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final String helpText;
    private final CommandExecutor commandExecutor;
    private final MessageSender sender;

    @Value("${bot.token}")
    private String botToken;

    public EventifyBot(CommandExecutor commandExecutor,
                       MessageSender sender) {

        this.commandExecutor = commandExecutor;
        this.sender = sender;
        helpText = "Не удалось распознать введенное значение.\n" + HelpCommand.TEXT;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingSingleThreadUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        try {
            handle(update);
        } catch (Exception e) {
            log.error("Ошибка обработки {}", update.getUpdateId(), e);
        }
    }

    private void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        var message = update.getMessage();
        var chatId = message.getChatId();
        var text = message.getText();

        var found = commandExecutor.tryExecute(chatId, text);
        if (!found) {
            sender.sendText(chatId, helpText);
        }
    }
}