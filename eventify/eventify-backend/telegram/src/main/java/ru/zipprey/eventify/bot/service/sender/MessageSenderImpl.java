package ru.zipprey.eventify.bot.service.sender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageSenderImpl implements MessageSender {

    private final TelegramClient client;

    @Override
    public void sendText(long chatId, String text) {
        var msg = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            client.execute(msg);
        } catch (TelegramApiException e) {
            log.warn("Ошибка отправки сообщения в чат {}", chatId, e);
        }
    }
}
