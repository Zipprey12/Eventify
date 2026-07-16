package ru.zipprey.eventify.notification.service.reminder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.notification.model.email.EmailTemplate;
import ru.zipprey.eventify.notification.repository.email.EventReminderRepository;
import ru.zipprey.eventify.notification.service.email.EmailDateFormatter;
import ru.zipprey.eventify.notification.service.pending.PendingEmailService;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventReminderScheduler {

    private final EventReminderRepository repository;
    private final EventReminderService reminderService;
    private final PendingEmailService pendingEmailService;

    @Scheduled(fixedDelayString = "${reminder.check-delay-ms:60000}")
    public void sendDueReminders() {
        var due = repository.findReminders(Instant.now());
        if (due.isEmpty()) {
            return;
        }

        for (var reminder : due) {
            try {
                var claimed = reminderService.claim(reminder.getId());
                if (!claimed) {
                    continue;
                }

                var args = Map.of(
                        "eventTitle", reminder.getEventTitle(),
                        "eventDateTime", EmailDateFormatter.format(reminder.getEventDateTime()),
                        "ticketsCount", String.valueOf(reminder.getTicketsCount())
                );
                pendingEmailService.enqueue(reminder.getCustomerEmail(), EmailTemplate.EVENT_REMINDER, args);
            } catch (Exception e) {
                log.error("Ошибка постановки в очередь напоминания id={}: {}", reminder.getId(), e.getMessage());
            }
        }
    }
}
