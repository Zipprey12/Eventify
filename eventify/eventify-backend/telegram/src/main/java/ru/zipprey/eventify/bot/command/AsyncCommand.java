package ru.zipprey.eventify.bot.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.zipprey.eventify.bot.service.sender.MessageSender;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@RequiredArgsConstructor
public abstract class AsyncCommand implements TelegramCommand {

    private final Executor executor;
    private final MessageSender sender;

    @Override
    public final void execute(long chatId, String[] args) {
        CompletableFuture.runAsync(() -> handle(chatId, args), executor)
                .exceptionally(e -> {
                    log.error("Ошибка выполнения команды {}", getKey(), e);
                    sender.sendText(chatId, "Произошла ошибка, попробуйте позже");
                    return null;
                });
    }

    protected abstract void handle(long chatId, String[] args);
}
