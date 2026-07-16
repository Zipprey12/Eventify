package ru.zipprey.eventify.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.zipprey.eventify.notification.repository.SettingsRepository;

@RestController
@RequestMapping("/user/notifications")
@RequiredArgsConstructor
public class EmailConfirmationController {

    private final SettingsRepository repository;

    @PostMapping("/confirm-email")
    public ResponseEntity<Void> confirm(@RequestParam String email, @RequestParam String code) {
        var settings = repository.findById(email).orElse(null);

        if (settings == null || settings.getVerificationCode() == null
                || !settings.getVerificationCode().equals(code)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (Boolean.TRUE.equals(settings.getEmailConfirmed())) {
            return ResponseEntity.ok().build();
        }

        settings.setEmailConfirmed(true);
        settings.setVerificationCode(null);
        repository.save(settings);

        return ResponseEntity.ok().build();
    }
}
