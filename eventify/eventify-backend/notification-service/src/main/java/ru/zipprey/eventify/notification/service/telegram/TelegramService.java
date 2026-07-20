package ru.zipprey.eventify.notification.service.telegram;

import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface TelegramService {

    String generateLinkCode(Authentication authentication);

    Optional<String> consumeLinkCode(long chatId, String code);
}