package ru.zipprey.eventify.notification.messaging.consumption.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.zipprey.eventify.kafka.booking.*;
import ru.zipprey.eventify.notification.messaging.consumption.MessageHandler;
import ru.zipprey.eventify.notification.service.email.EmailService;
import ru.zipprey.eventify.notification.service.reminder.EventReminderService;

import java.util.List;

@Slf4j
@Service
public class BookingMessageHandler extends MessageHandler {

    public BookingMessageHandler(EmailService emailService, EventReminderService reminderService) {
        super(emailService, reminderService);
    }

    public void handle(BookingDeletedMessage message) {
        var bookingId = message.bookingId();
        safeExecute(() -> getReminderService().cancel(bookingId),
                "cancel, bookingId=" + bookingId);
    }

    public void handle(BookingsOutcompetedMessage message) {
        getEmailService().notify(message);
        safeExecute(() -> deleteAllReminds(message.bookings()),
                "cancelAll, operationId=" + message.operationId());
    }

    public void handle(BookingsCascadeCanceledMessage message) {
        getEmailService().notify(message);
        safeExecute(() -> deleteAllReminds(message.bookings()),
                "cancelAll, eventId=" + message.eventId());
    }

    public void handle(BookingConfirmedMessage message) {
        var bookingId = message.bookingId();
        getEmailService().notify(message);
        safeExecute(() -> getReminderService().scheduleIfRequested(message),
                "scheduleIfRequested, bookingId=" + bookingId);
    }

    public void handle(BookingDeletedByAdminMessage message) {
        var bookingId = message.bookingId();
        getEmailService().notify(message);
        safeExecute(() -> getReminderService().cancel(bookingId),
                "cancel, bookingId=" + bookingId);
    }

    private void deleteAllReminds(List<CanceledBookingEntry> entries) {
        var ids = entries.stream()
                .map(CanceledBookingEntry::bookingId)
                .toList();
        getReminderService().cancelAll(ids);
    }
}
