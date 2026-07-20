package ru.zipprey.eventify.bot.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.bot.model.dto.DueReminder;
import ru.zipprey.eventify.bot.model.entity.SentReminder;
import ru.zipprey.eventify.bot.repository.SentReminderRepository;
import ru.zipprey.eventify.bot.service.sender.MessageSender;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderProcessor {

    private final MessageSender sender;
    private final SentReminderRepository sentReminderRepository;

    @Async("botTaskExecutor")
    public void processAsync(DueReminder reminder) {
        if (sentReminderRepository.existsByBookingIdAndChatId(reminder.bookingId(), reminder.chatId())) {
            return;
        }

        sender.sendText(reminder.chatId(), formatText(reminder));

        try {
            var entity = new SentReminder();
            entity.setBookingId(reminder.bookingId());
            entity.setChatId(reminder.chatId());
            entity.setSentAt(Instant.now());
            sentReminderRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.debug("Напоминание для booking={}, chat={} уже было отправлено",
                    reminder.bookingId(), reminder.chatId());
        }
    }

    private String formatText(DueReminder reminder) {
        var sb = new StringBuilder("Напоминаем о предстоящем событии:\n\n")
                .append(reminder.eventTitle());

        if (reminder.eventDescription() != null && !reminder.eventDescription().isBlank()) {
            sb.append("\n\n").append(reminder.eventDescription());
        }
        return sb.toString();
    }
}
