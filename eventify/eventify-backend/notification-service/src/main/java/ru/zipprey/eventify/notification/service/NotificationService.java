package ru.zipprey.eventify.notification.service;

import org.springframework.security.core.Authentication;
import ru.zipprey.eventify.notification.model.dto.SettingsDto;

public interface NotificationService {

    SettingsDto getOrCreate(Authentication authentication);

    SettingsDto update(SettingsDto dto, Authentication authentication);

    void delete(Authentication authentication);
}
