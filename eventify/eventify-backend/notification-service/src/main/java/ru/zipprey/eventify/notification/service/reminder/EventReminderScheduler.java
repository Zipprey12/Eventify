package ru.zipprey.eventify.notification.service.reminder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.notification.repository.EventReminderRepository;
import ru.zipprey.eventify.notification.service.email.EmailMessageFormatter;
import ru.zipprey.eventify.notification.service.email.EmailSender;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventReminderScheduler {

    private final EventReminderRepository repository;
    private final EmailMessageFormatter formatter;
    private final EmailSender sender;

    @Scheduled(fixedDelayString = "${reminder.check-delay-ms:60000}")
    public void sendDueReminders() {
        var due = repository.findReminders(Instant.now());
        if (due.isEmpty()) {
            return;
        }

        for (var reminder : due) {
            try {
                var claimed = repository.markSentIfNotAlready(reminder.getId());
                if (claimed == 0) {
                    continue;
                }

                var dto = formatter.formatReminder(reminder);
                sender.send(reminder.getCustomerEmail(), dto);
            } catch (Exception e) {
                log.error("Ошибка отправки напоминания id={}: {}", reminder.getId(), e.getMessage());
            }
        }
    }
}
