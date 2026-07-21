package ru.zipprey.eventify.notification.messaging.consumption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.kafka.booking.*;
import ru.zipprey.eventify.notification.messaging.consumption.deduplicate.KafkaDeduplicateService;
import ru.zipprey.eventify.notification.service.email.EmailService;
import ru.zipprey.eventify.notification.service.reminder.EventReminderService;

import java.util.List;

@Slf4j
@Component
public class BookingMessageConsumer extends MessageConsumer {

    private static final String BOOKING_CANCELED = "booking.canceled";
    private static final String BOOKING_FORCE_CANCELED = "booking.force-canceled";
    private static final String BOOKING_CASCADE_CANCELED = "booking.canceled-cascade";
    private static final String BOOKING_CONFIRMED = "booking.confirmed";
    private static final String BOOKING_DELETED_BY_ADMIN = "booking.deleted-by-admin";

    private final EventReminderService reminderService;

    public BookingMessageConsumer(EmailService emailService,
                                  KafkaDeduplicateService deduplicateService,
                                  EventReminderService reminderService) {
        super(deduplicateService, emailService);
        this.reminderService = reminderService;
    }

    @KafkaListener(topics = BOOKING_CANCELED, groupId = "notification-service-booking-canceled")
    public void handleBookingCanceled(BookingDeletedMessage message) {
        logMessage(BOOKING_CANCELED, message);

        var bookingId = message.bookingId();
        if (getDeduplicateService().isDuplicate(BOOKING_CANCELED, bookingId)) {
            logDuplicate(BOOKING_CANCELED, bookingId);
            return;
        }
        safeExecute(() -> reminderService.cancel(bookingId), "cancel, bookingId=" + bookingId);
    }

    @KafkaListener(topics = BOOKING_FORCE_CANCELED, groupId = "notification-service-booking-force-canceled")
    public void handleBookingCanceled(BookingsOutcompetedMessage message) {
        logMessage(BOOKING_FORCE_CANCELED, message);

        var id = message.operationId();
        if (getDeduplicateService().isDuplicate(BOOKING_FORCE_CANCELED, id)) {
            logDuplicate(BOOKING_FORCE_CANCELED, id);
            return;
        }

        getEmailService().notify(message);
        safeExecute(() -> deleteAllReminds(message.bookings()), "cancelAll, operationId=" + id);
    }

    @KafkaListener(topics = BOOKING_CASCADE_CANCELED, groupId = "notification-service-booking-cascade-canceled")
    public void handleBookingCascadeCanceled(BookingsCascadeCanceledMessage message) {
        logMessage(BOOKING_CASCADE_CANCELED, message);

        var eventId = message.eventId();
        if (getDeduplicateService().isDuplicate(BOOKING_CASCADE_CANCELED, eventId)) {
            logDuplicate(BOOKING_CASCADE_CANCELED, eventId);
            return;
        }
        getEmailService().notify(message);
        safeExecute(() -> deleteAllReminds(message.bookings()), "cancelAll, eventId=" + eventId);
    }

    @KafkaListener(topics = BOOKING_CONFIRMED, groupId = "notification-service-booking-confirmed")
    public void handleBookingConfirmed(BookingConfirmedMessage message) {
        logMessage(BOOKING_CONFIRMED, message);

        var bookingId = message.bookingId();
        if (getDeduplicateService().isDuplicate(BOOKING_CONFIRMED, bookingId)) {
            logDuplicate(BOOKING_CONFIRMED, bookingId);
            return;
        }

        getEmailService().notify(message);
        safeExecute(() -> reminderService.scheduleIfRequested(message), "scheduleIfRequested, bookingId=" + bookingId);
    }

    @KafkaListener(topics = BOOKING_DELETED_BY_ADMIN, groupId = "notification-service-booking-deleted-by-admin")
    public void handleBookingDeletedByAdmin(BookingDeletedByAdminMessage message) {
        logMessage(BOOKING_DELETED_BY_ADMIN, message);

        var bookingId = message.bookingId();
        if (getDeduplicateService().isDuplicate(BOOKING_DELETED_BY_ADMIN, bookingId)) {
            logDuplicate(BOOKING_DELETED_BY_ADMIN, bookingId);
            return;
        }

        getEmailService().notify(message);
        safeExecute(() -> reminderService.cancel(bookingId), "cancel, bookingId=" + bookingId);
    }

    private void deleteAllReminds(List<CanceledBookingEntry> entries) {
        var ids = entries.stream()
                .map(CanceledBookingEntry::bookingId)
                .toList();
        reminderService.cancelAll(ids);
    }
}