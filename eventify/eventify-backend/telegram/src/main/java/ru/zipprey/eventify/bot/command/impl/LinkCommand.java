package ru.zipprey.eventify.bot.command.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.bot.command.AsyncCommand;
import ru.zipprey.eventify.bot.message.caller.NotificationServiceCaller;
import ru.zipprey.eventify.bot.model.entity.TelegramSubscription;
import ru.zipprey.eventify.bot.repository.TelegramSubscriptionRepository;
import ru.zipprey.eventify.bot.service.sender.MessageSender;

import java.util.concurrent.Executor;

@Component
public class LinkCommand extends AsyncCommand {
    private static final String KEY = "/start";
    private static final int DEFAULT_NOTIFY_BEFORE_HOURS = 24;

    private final MessageSender sender;
    private final NotificationServiceCaller notificationCaller;
    private final TelegramSubscriptionRepository subscriptionRepository;

    public LinkCommand(@Qualifier("botTaskExecutor") Executor executor,
                       MessageSender sender,
                       NotificationServiceCaller notificationCaller,
                       TelegramSubscriptionRepository subscriptionRepository) {
        super(executor, sender);
        this.sender = sender;
        this.notificationCaller = notificationCaller;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    protected void handle(long chatId, String[] args) {
        if (args.length == 0) {
            sender.sendText(chatId, "Добро пожаловать в Eventify!\n\n" +
                    "Чтобы получать уведомления о бронированиях, привяжите аккаунт: " +
                    "получите код в настройках уведомлений на сайте Eventify и отправьте " +
                    "/start <код>.\n\n" + HelpCommand.TEXT);
            return;
        }

        var email = notificationCaller.link(chatId, args[0]);
        if (email.isEmpty()) {
            sender.sendText(chatId, "Код недействителен или устарел. Получите новый код на сайте и повторите.");
            return;
        }

        subscriptionRepository.save(TelegramSubscription.builder()
                .chatId(chatId)
                .customerEmail(email.get())
                .notifyBeforeHours(DEFAULT_NOTIFY_BEFORE_HOURS)
                .build());

        sender.sendText(chatId, "Telegram успешно привязан к аккаунту " + email.get() + ".\n\n" + HelpCommand.TEXT);
    }
}