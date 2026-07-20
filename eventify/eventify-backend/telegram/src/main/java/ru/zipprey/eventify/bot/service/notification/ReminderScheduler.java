package ru.zipprey.eventify.bot.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final ReminderQueue queue;
    private final ReminderProcessor processor;

    @Value("${reminder.batch-size}")
    private int batchSize;

    @Scheduled(fixedDelayString = "${reminder.poll-interval-ms}")
    public void poll() {
        var due = queue.dequeueDue(Instant.now(), batchSize);
        due.forEach(processor::processAsync);
    }
}
