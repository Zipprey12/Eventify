package ru.zipprey.eventify.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.zipprey.eventify.notification.exception.SettingsNotFoundException;
import ru.zipprey.eventify.notification.mapper.SettingMapper;
import ru.zipprey.eventify.notification.model.dto.SettingsDto;
import ru.zipprey.eventify.notification.model.enity.Settings;
import ru.zipprey.eventify.notification.repository.SettingsRepository;
import ru.zipprey.eventify.notification.service.reminder.EventReminderService;

@Service
@RequiredArgsConstructor
public class SettingsServiceImpl implements SettingsService {

    private final SettingsRepository repository;
    private final SettingMapper mapper;
    private final EventReminderService reminderService;

    private static Settings createDefault(String email) {
        return Settings.builder()
                .customerEmail(email)
                .notifyBeforeHours(24)
                .notifyNewEvents(true)
                .notifyUpcoming(true)
                .emailConfirmed(false)
                .build();
    }

    @Override
    public SettingsDto getOrCreate(Authentication authentication) {
        var email = EmailValidator.getEmail(authentication);
        var setting = repository.findById(email)
                .orElseGet(() -> {
                            var created = createDefault(email);
                            return repository.save(created);
                        }
                );

        return mapper.toDto(setting);
    }

    @Override
    public SettingsDto update(SettingsDto dto, Authentication authentication) {
        var email = EmailValidator.getEmail(authentication);
        var entity = repository.findById(email)
                .orElseThrow(() -> new SettingsNotFoundException(email));

        mapper.updateEntity(dto, entity);

        var saved = repository.save(entity);
        reminderService.rescheduleForSettingsChange(email, saved.getNotifyUpcoming(), saved.getNotifyBeforeHours());
        return mapper.toDto(saved);
    }

    @Override
    public void delete(Authentication authentication) {
        var email = EmailValidator.getEmail(authentication);
        if (repository.existsById(email)) {
            repository.deleteById(email);
            reminderService.rescheduleForSettingsChange(email, false, null);
        }
    }
}
