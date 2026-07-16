package ru.zipprey.eventify.notification.service.reminder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.notification.repository.email.EventReminderRepository;
import ru.zipprey.eventify.notification.service.pending.PendingEmailService;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventReminderScheduler {

    private final EventReminderRepository repository;
    private final EventReminderService reminderService;
    private final PendingEmailService pendingEmailService;

    @Scheduled(fixedDelayString = "${reminder.check-delay-ms:60000}")
    public void sendDueReminders() {
        var reminders = repository.findReminders(Instant.now());
        if (reminders.isEmpty()) {
            return;
        }

        for (var reminder : reminders) {
            try {
                reminderService.claimAndEnqueueReminder(reminder);
            } catch (Exception e) {
                log.error("Ошибка постановки в очередь напоминания id={}: {}", reminder.getId(), e.getMessage());
            }
        }
    }
}
