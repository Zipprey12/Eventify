package ru.zipprey.eventify.notification.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zipprey.eventify.notification.model.email.EmailTemplate;
import ru.zipprey.eventify.notification.model.enity.Settings;
import ru.zipprey.eventify.notification.repository.SettingsRepository;
import ru.zipprey.eventify.notification.service.pending.PendingEmailService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailConfirmationService {

    private final SettingsRepository settingsRepository;
    private final PendingEmailService pendingEmailService;

    @Value("${booking-ui.base-url}")
    private String frontendBaseUrl;

    @Transactional
    public void sendConfirmation(String email) {
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
