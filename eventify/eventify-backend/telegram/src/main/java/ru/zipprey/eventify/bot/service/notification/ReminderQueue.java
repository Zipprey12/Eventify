package ru.zipprey.eventify.bot.service.notification;

import ru.zipprey.eventify.bot.model.dto.DueReminder;

import java.time.Instant;
import java.util.List;

public interface ReminderQueue {

    void add(long bookingId, long chatId, Instant notifyAt, String eventTitle, String eventDescription);

    void remove(long bookingId, long chatId);

    List<DueReminder> dequeueDue(Instant instant, int maxCount);

    void clear();
}
