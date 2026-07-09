package ru.zipprey.eventify.notification.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.zipprey.eventify.notification.model.dto.SettingsDto;
import ru.zipprey.eventify.notification.service.NotificationService;

@RestController
@RequestMapping("/user/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping
    public SettingsDto getSettings(Authentication authentication) {
        return service.getOrCreate(authentication);
    }

    @PutMapping
    public SettingsDto updateSettings(@Valid @RequestBody SettingsDto dto,
                                      Authentication authentication) {

        return service.update(dto, authentication);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication) {
        service.delete(authentication);
    }
}
