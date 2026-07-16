package ru.zipprey.eventify.notification.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.kafka.booking.BookingConfirmedMessage;
import ru.zipprey.eventify.kafka.booking.BookingDeletedByAdminMessage;
import ru.zipprey.eventify.kafka.booking.BookingsCascadeCanceledMessage;
import ru.zipprey.eventify.kafka.booking.BookingsOutcompetedMessage;
import ru.zipprey.eventify.kafka.event.EventCreatedMessage;
import ru.zipprey.eventify.kafka.event.EventDateChangedMessage;
import ru.zipprey.eventify.notification.messaging.caller.BookingServiceCaller;
import ru.zipprey.eventify.notification.model.email.EmailTemplate;
import ru.zipprey.eventify.notification.repository.SettingsRepository;
import ru.zipprey.eventify.notification.service.pending.PendingEmailService;

import java.util.Map;


@Component
@RequiredArgsConstructor
public class EmailService {

    public static final String TICKETS_COUNT = "ticketsCount";
    public static final String EVENT_TITLE = "eventTitle";
    public static final String EVENT_DATE_TIME = "eventDateTime";

    private final PendingEmailService pendingEmailService;
    private final SettingsRepository settingsRepository;
    private final BookingServiceCaller bookingsCaller;

    public void notify(EventCreatedMessage message) {
        var args = Map.of(
                "title", message.title(),
                "description", message.description(),
                "dateTime", EmailDateFormatter.format(message.dateTime())
        );
        enqueueToSubscribers(EmailTemplate.EVENT_CREATED, args);
    }

    public void notify(EventDateChangedMessage message) {
        var args = Map.of(
                EVENT_TITLE, message.eventTitle(),
                "newDateTime", EmailDateFormatter.format(message.newDateTime())
        );

        var emails = bookingsCaller.findCustomerEmailsByEventId(message.eventId()).block();
        if (emails == null) {
            return;
        }
        for (var email : emails) {
            pendingEmailService.enqueue(email, EmailTemplate.EVENT_DATE_CHANGED, args);
        }
    }

    public void notify(BookingConfirmedMessage message) {
        var args = Map.of(
                EVENT_TITLE, message.eventTitle(),
                TICKETS_COUNT, String.valueOf(message.ticketsCount()),
                EVENT_DATE_TIME, EmailDateFormatter.format(message.eventDateTime())
        );
        pendingEmailService.enqueue(message.customerEmail(), EmailTemplate.BOOKING_CONFIRMED, args);
    }

    public void notify(BookingDeletedByAdminMessage message) {
        var args = Map.of(
                EVENT_TITLE, message.eventTitle(),
                TICKETS_COUNT, String.valueOf(message.ticketsCount()),
                EVENT_DATE_TIME, EmailDateFormatter.format(message.eventDateTime())
        );
        pendingEmailService.enqueue(message.customerEmail(), EmailTemplate.BOOKING_DELETED_BY_ADMIN, args);
    }

    public void notify(BookingsCascadeCanceledMessage message) {
        for (var booking : message.bookings()) {
            var args = Map.of(
                    EVENT_TITLE, message.eventTitle(),
                    TICKETS_COUNT, String.valueOf(booking.ticketsCount())
            );
            pendingEmailService.enqueue(booking.customerEmail(), EmailTemplate.BOOKING_CASCADE_CANCELED, args);
        }
    }

    public void notify(BookingsOutcompetedMessage message) {
        for (var booking : message.bookings()) {
            var args = Map.of(
                    EVENT_TITLE, message.eventTitle(),
                    TICKETS_COUNT, String.valueOf(booking.ticketsCount())
            );
            pendingEmailService.enqueue(booking.customerEmail(), EmailTemplate.BOOKING_OUTCOMPETED, args);
        }
    }

    private void enqueueToSubscribers(EmailTemplate template, Map<String, String> args) {
        var subscribers = settingsRepository.findAllByNotifyNewEventsTrue();
        for (var settings : subscribers) {
            pendingEmailService.enqueue(settings.getCustomerEmail(), template, args);
        }
    }
}
