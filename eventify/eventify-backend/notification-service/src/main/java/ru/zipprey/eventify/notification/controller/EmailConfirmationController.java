package ru.zipprey.eventify.notification.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.zipprey.eventify.notification.exception.EmailConfirmException;
import ru.zipprey.eventify.notification.repository.SettingsRepository;
import ru.zipprey.eventify.notification.service.EmailValidator;
import ru.zipprey.eventify.notification.service.email.EmailConfirmationService;

@Slf4j
@RestController
@RequestMapping("/user/notifications")
@RequiredArgsConstructor
public class EmailConfirmationController {

    private final SettingsRepository repository;
    private final EmailConfirmationService emailConfirmationService;

    @PostMapping("/confirm-email")
    public void confirm(@RequestParam String email, @RequestParam String code) {
        log.info("Переход по ссылке подтверждения email: {}", email);

        var settings = repository.findById(email).orElse(null);

        if (settings == null || settings.getVerificationCode() == null
                || !settings.getVerificationCode().equals(code)) {

            log.warn("Ошибка подтверждения почты: {}", email);
            throw new EmailConfirmException();
        }

        if (Boolean.TRUE.equals(settings.getEmailConfirmed())) {
            log.info("Подтверждена почта: {}", email);
        }

        settings.setEmailConfirmed(true);
        settings.setVerificationCode(null);
        repository.save(settings);
    }

    @PostMapping("/resend-confirmation")
    public void resendConfirmation(Authentication authentication) {
        var email = EmailValidator.getEmail(authentication);
        log.info("Повторный запрос письма для подтверждения: {}", email);
        emailConfirmationService.sendConfirmation(email);
    }
}