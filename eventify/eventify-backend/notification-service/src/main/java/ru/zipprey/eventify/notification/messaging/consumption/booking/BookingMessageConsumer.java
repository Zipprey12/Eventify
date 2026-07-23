package ru.zipprey.eventify.notification.messaging.consumption.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.kafka.booking.*;
import ru.zipprey.eventify.notification.messaging.consumption.MessageConsumer;
import ru.zipprey.eventify.notification.messaging.consumption.deduplicate.KafkaDeduplicateService;

@Slf4j
@Component
public class BookingMessageConsumer extends MessageConsumer {

    private static final String BOOKING_CANCELED = "booking.canceled";
    private static final String BOOKING_FORCE_CANCELED = "booking.force-canceled";
    private static final String BOOKING_CASCADE_CANCELED = "booking.canceled-cascade";
    private static final String BOOKING_CONFIRMED = "booking.confirmed";
    private static final String BOOKING_DELETED_BY_ADMIN = "booking.deleted-by-admin";

    private final BookingMessageHandler handler;

    public BookingMessageConsumer(KafkaDeduplicateService deduplicateService,
                                  BookingMessageHandler handler) {
        super(deduplicateService);
        this.handler = handler;
    }

    @KafkaListener(topics = BOOKING_CANCELED, groupId = "notification-service-booking-canceled")
    public void handleBookingCanceled(BookingDeletedMessage message) {
        logConsumption(BOOKING_CANCELED, message);

        var bookingId = message.bookingId();
        if (getDeduplicateService().isDuplicate(BOOKING_CANCELED, bookingId)) {
            logDuplicate(BOOKING_CANCELED, bookingId);
            return;
        }
        handler.handle(message);
    }

    @KafkaListener(topics = BOOKING_FORCE_CANCELED, groupId = "notification-service-booking-force-canceled")
    public void handleBookingCanceled(BookingsOutcompetedMessage message) {
        logConsumption(BOOKING_FORCE_CANCELED, message);

        var id = message.operationId();
        if (getDeduplicateService().isDuplicate(BOOKING_FORCE_CANCELED, id)) {
            logDuplicate(BOOKING_FORCE_CANCELED, id);
            return;
        }
        handler.handle(message);
    }

    @KafkaListener(topics = BOOKING_CASCADE_CANCELED, groupId = "notification-service-booking-cascade-canceled")
    public void handleBookingCascadeCanceled(BookingsCascadeCanceledMessage message) {
        logConsumption(BOOKING_CASCADE_CANCELED, message);

        var eventId = message.eventId();
        if (getDeduplicateService().isDuplicate(BOOKING_CASCADE_CANCELED, eventId)) {
            logDuplicate(BOOKING_CASCADE_CANCELED, eventId);
            return;
        }
        handler.handle(message);
    }

    @KafkaListener(topics = BOOKING_CONFIRMED, groupId = "notification-service-booking-confirmed")
    public void handleBookingConfirmed(BookingConfirmedMessage message) {
        logConsumption(BOOKING_CONFIRMED, message);

        var bookingId = message.bookingId();
        if (getDeduplicateService().isDuplicate(BOOKING_CONFIRMED, bookingId)) {
            logDuplicate(BOOKING_CONFIRMED, bookingId);
            return;
        }
        handler.handle(message);
    }

    @KafkaListener(topics = BOOKING_DELETED_BY_ADMIN, groupId = "notification-service-booking-deleted-by-admin")
    public void handleBookingDeletedByAdmin(BookingDeletedByAdminMessage message) {
        logConsumption(BOOKING_DELETED_BY_ADMIN, message);

        var bookingId = message.bookingId();
        if (getDeduplicateService().isDuplicate(BOOKING_DELETED_BY_ADMIN, bookingId)) {
            logDuplicate(BOOKING_DELETED_BY_ADMIN, bookingId);
            return;
        }
        handler.handle(message);
    }
}