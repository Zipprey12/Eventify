package ru.zipprey.eventify.notification.messaging.consumption;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.zipprey.eventify.notification.service.email.EmailService;
import ru.zipprey.eventify.notification.service.reminder.EventReminderService;

@Slf4j
@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public abstract class MessageHandler {

    private final EmailService emailService;
    private final EventReminderService reminderService;

    protected void safeExecute(Runnable action, String context) {
        try {
            action.run();
        } catch (Exception e) {
            log.error("Ошибка при работе с напоминаниями ({}): {}", context, e.getMessage());
        }
    }
}
