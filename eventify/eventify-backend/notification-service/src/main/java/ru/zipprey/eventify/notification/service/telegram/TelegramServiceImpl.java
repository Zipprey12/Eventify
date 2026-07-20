package ru.zipprey.eventify.notification.service.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zipprey.eventify.notification.model.enity.TelegramLink;
import ru.zipprey.eventify.notification.repository.telegram.TelegramLinkRepository;
import ru.zipprey.eventify.notification.service.EmailValidator;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TelegramServiceImpl implements TelegramService {

    public static final Duration expireDuration = Duration.ofMinutes(15);

    private final TelegramLinkRepository repository;

    @Override
    public String generateLinkCode(Authentication authentication) {
        var email = EmailValidator.getEmail(authentication);

        var link = repository.findById(email)
                .orElseGet(() -> {
                    var created = new TelegramLink();
                    created.setCustomerEmail(email);
                    return created;
                });

        var code = UUID.randomUUID().toString();
        link.setLinkCode(code);
        link.setLinkCodeExpiresAt(Instant.now().plus(expireDuration));

        repository.save(link);
        return code;
    }

    @Override
    @Transactional
    public Optional<String> consumeLinkCode(long chatId, String code) {
        var link = repository.findByLinkCode(code).orElse(null);

        if (link == null || link.getLinkCodeExpiresAt() == null
                || link.getLinkCodeExpiresAt().isBefore(Instant.now())) {
            return Optional.empty();
        }

        link.setChatId(chatId);
        link.setLinkCode(null);
        link.setLinkCodeExpiresAt(null);
        repository.save(link);

        return Optional.of(link.getCustomerEmail());
    }
}