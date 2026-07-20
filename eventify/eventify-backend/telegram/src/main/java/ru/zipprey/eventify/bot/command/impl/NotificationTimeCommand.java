package ru.zipprey.eventify.bot.command.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.bot.command.AsyncCommand;
import ru.zipprey.eventify.bot.repository.TelegramSubscriptionRepository;
import ru.zipprey.eventify.bot.service.sender.MessageSender;

import java.util.Optional;
import java.util.concurrent.Executor;

@Component
public class NotificationTimeCommand extends AsyncCommand {

    private static final int MIN_HOURS = 1;
    private static final int MAX_HOURS = 24;
    private static final String KEY = "/notification_time";

    private final MessageSender sender;
    private final TelegramSubscriptionRepository subscriptionRepository;

    public NotificationTimeCommand(@Qualifier("botTaskExecutor") Executor executor,
                                   MessageSender sender,
                                   TelegramSubscriptionRepository subscriptionRepository) {
        super(executor, sender);
        this.sender = sender;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    protected void handle(long chatId, String[] args) {
        var hours = parseHours(args);
        if (hours.isEmpty()) {
            sender.sendText(chatId, "Формат: /notification_time <часы, 1..24>. Например: /notification_time 12");
            return;
        }

        var subscription = subscriptionRepository.findById(chatId).orElse(null);
        if (subscription == null) {
            sender.sendText(chatId, "Аккаунт не привязан. Привяжите Telegram на сайте Eventify.");
            return;
        }

        subscription.setNotifyBeforeHours(hours.get());
        subscriptionRepository.save(subscription);

        sender.sendText(chatId, "Напоминания в Telegram будут приходить за " + hours.get() +
                " ч. до события.\nНа уведомления по email это не влияет.");
    }

    private Optional<Integer> parseHours(String[] args) {
        if (args.length == 0) {
            return Optional.empty();
        }
        try {
            var hours = Integer.parseInt(args[0]);
            if (hours < MIN_HOURS || hours > MAX_HOURS) {
                return Optional.empty();
            }
            return Optional.of(hours);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
