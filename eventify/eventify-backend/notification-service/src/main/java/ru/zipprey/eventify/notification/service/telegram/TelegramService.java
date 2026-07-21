package ru.zipprey.eventify.notification.service.telegram;

import org.springframework.security.core.Authentication;

public interface TelegramService {

    String generateLinkCode(Authentication authentication);

    String consumeLinkCode(long chatId, String code);

    void unlink(long chatId);
}