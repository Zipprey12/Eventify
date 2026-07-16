package ru.zipprey.eventify.notification.service.reminder;

import ru.zipprey.eventify.kafka.booking.BookingConfirmedMessage;
import ru.zipprey.eventify.notification.model.enity.EventReminder;

import java.time.Instant;
import java.util.List;

public interface EventReminderService {

    void scheduleIfRequested(BookingConfirmedMessage message);

    void cancel(Long bookingId);

    void cancelAll(List<Long> bookingIds);

    void rescheduleForEventDateChange(Long eventId, Instant newEventDateTime);

    void rescheduleForSettingsChange(String customerEmail, Boolean notifyUpcoming, Integer notifyBeforeHours);

    void claimAndEnqueueReminder(EventReminder reminder);
}