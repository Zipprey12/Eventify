package ru.zipprey.eventify.notification.messaging.consumption.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.kafka.auth.UserRegisteredMessage;
import ru.zipprey.eventify.notification.messaging.consumption.deduplicate.KafkaDeduplicateService;
import ru.zipprey.eventify.notification.service.email.EmailConfirmationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserMessageConsumer {

    private static final String USER_REGISTERED = "user.registered";

    private final KafkaDeduplicateService deduplicateService;
    private final EmailConfirmationService emailConfirmationService;

    @KafkaListener(topics = USER_REGISTERED, groupId = "notification-service-user-registered")
    public void handle(UserRegisteredMessage message) {
        log.info("Получено {} : {}", USER_REGISTERED, message);

        var email = message.email();
        if (deduplicateService.isDuplicate(USER_REGISTERED, email)) {
            log.info("Дубль {}: email={}, пропуск операции", USER_REGISTERED, email);
            return;
        }

        emailConfirmationService.sendConfirmation(email);
    }
}
