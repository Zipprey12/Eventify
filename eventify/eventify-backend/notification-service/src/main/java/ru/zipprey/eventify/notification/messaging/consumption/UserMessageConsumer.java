package ru.zipprey.eventify.notification.messaging.consumption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.kafka.auth.UserRegisteredMessage;
import ru.zipprey.eventify.notification.messaging.consumption.deduplicate.KafkaDeduplicateService;
import ru.zipprey.eventify.notification.model.email.EmailTemplate;
import ru.zipprey.eventify.notification.model.enity.Settings;
import ru.zipprey.eventify.notification.repository.SettingsRepository;
import ru.zipprey.eventify.notification.service.pending.PendingEmailService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserMessageConsumer {

    private static final String USER_REGISTERED = "user.registered";

    private final SettingsRepository settingsRepository;
    private final PendingEmailService pendingEmailService;
    private final KafkaDeduplicateService deduplicateService;

    @Value("${booking-ui.base-url}")
    private String frontendBaseUrl;

    @KafkaListener(topics = USER_REGISTERED, groupId = "notification-service-user-registered")
    public void handle(UserRegisteredMessage message) {
        log.info("Получено {} : {}", USER_REGISTERED, message);

        var email = message.email();
        if (deduplicateService.isDuplicate(USER_REGISTERED, email)) {
            log.info("Дубль {}: email={}, пропуск операции", USER_REGISTERED, email);
            return;
        }

        var settings = settingsRepository.findById(email).orElseGet(() -> Settings.builder()
                .customerEmail(email)
                .notifyNewEvents(true)
                .notifyUpcoming(true)
                .notifyBeforeHours(24)
                .build());

        var code = UUID.randomUUID().toString();
        settings.setEmailConfirmed(false);
        settings.setVerificationCode(code);
        settingsRepository.save(settings);

        var link = frontendBaseUrl + "/confirm-email?email="
                + URLEncoder.encode(email, StandardCharsets.UTF_8)
                + "&code=" + code;

        pendingEmailService.enqueue(email, EmailTemplate.EMAIL_CONFIRMATION, Map.of("link", link));
    }
}
