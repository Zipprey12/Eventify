package ru.zipprey.eventify.notification.service.pending;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.notification.model.email.PendingEmailStatus;
import ru.zipprey.eventify.notification.model.enity.PendingEmail;
import ru.zipprey.eventify.notification.model.enity.Settings;
import ru.zipprey.eventify.notification.repository.SettingsRepository;
import ru.zipprey.eventify.notification.repository.email.PendingEmailRepository;
import ru.zipprey.eventify.notification.service.email.EmailSender;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PendingEmailScheduler {

    private final PendingEmailRepository repository;
    private final SettingsRepository settingsRepository;
    private final PendingEmailService pendingEmailService;
    private final EmailSender sender;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelayString = "${email.check-delay-ms:10000}")
    public void sendPending() {
        var pending = repository.findAllByStatus(PendingEmailStatus.PENDING);
        if (pending.isEmpty()) {
            return;
        }

        for (var email : pending) {
            try {
                process(email);
            } catch (Exception e) {
                log.error("Ошибка отправки письма id={} to={}: {}", email.getId(), email.getRecipient(), e.getMessage());
            }
        }
    }

    private void process(PendingEmail pending) {
        if (pending.getTemplate().isRequiresConfirmation() && !isConfirmed(pending.getRecipient())) {
            pendingEmailService.markSkippedUnconfirmed(pending.getId());
            log.info("Email {} не подтверждён — письмо {} пропущено",
                    pending.getRecipient(), pending.getTemplate());
            return;
        }

        var args = deserialize(pending.getArgumentsJson());
        var dto = pending.getTemplate().generate(args);
        var to = pending.getRecipient();

        sender.send(to, dto);
        pendingEmailService.markSent(pending.getId());
    }

    private boolean isConfirmed(String email) {
        return settingsRepository.findById(email)
                .map(Settings::getEmailConfirmed)
                .filter(Boolean.TRUE::equals)
                .isPresent();
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> deserialize(String json) {
        return objectMapper.readValue(json, Map.class);
    }
}
