package ru.zipprey.eventify.bot.command.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.bot.command.AsyncCommand;
import ru.zipprey.eventify.bot.message.caller.NotificationServiceCaller;
import ru.zipprey.eventify.bot.repository.SentReminderRepository;
import ru.zipprey.eventify.bot.repository.TelegramSubscriptionRepository;
import ru.zipprey.eventify.bot.service.sender.MessageSender;

import java.util.concurrent.Executor;

@Slf4j
@Component
public class StopCommand extends AsyncCommand {

    public static final String KEY = "/stop";
    public static final String TEXT = """
            Telegram отвязан, напоминания больше не будут приходить.
            Чтобы снова получать их, привяжите аккаунт на сайте
            """;

    private final MessageSender sender;
    private final NotificationServiceCaller notificationCaller;
    private final SentReminderRepository sentReminderRepository;
    private final TelegramSubscriptionRepository subscriptionRepository;

    public StopCommand(@Qualifier("botTaskExecutor") Executor executor,
                       MessageSender sender,
                       NotificationServiceCaller notificationCaller,
                       SentReminderRepository sentReminderRepository,
                       TelegramSubscriptionRepository subscriptionRepository) {
        super(executor, sender);
        this.sender = sender;
        this.notificationCaller = notificationCaller;
        this.sentReminderRepository = sentReminderRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    protected void handle(long chatId, String[] args) {
        try {
            notificationCaller.unlink(chatId);
        } catch (Exception e) {
            log.warn("Не удалось уведомить notification-service об отвязке chatId={}: {}", chatId, e.getMessage());
        }

        sentReminderRepository.deleteByChatId(chatId);
        subscriptionRepository.deleteById(chatId);

        sender.sendText(chatId, TEXT);
    }
}
