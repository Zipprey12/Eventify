package ru.zipprey.eventify.notification.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.kafka.booking.BookingConfirmedMessage;
import ru.zipprey.eventify.kafka.booking.BookingDeletedByAdminMessage;
import ru.zipprey.eventify.kafka.booking.BookingsCascadeCanceledMessage;
import ru.zipprey.eventify.kafka.booking.BookingsOutcompetedMessage;
import ru.zipprey.eventify.kafka.event.EventCreatedMessage;
import ru.zipprey.eventify.kafka.event.EventDateChangedMessage;
import ru.zipprey.eventify.notification.messaging.caller.BookingServiceCaller;
import ru.zipprey.eventify.notification.model.email.EmailMessageDto;
import ru.zipprey.eventify.notification.repository.SettingsRepository;


@Component
@RequiredArgsConstructor
@Async("emailTaskExecutor")
public class EmailService {

    private final EmailSender sender;
    private final EmailMessageFormatter formatter;
    private final SettingsRepository settingsRepository;
    private final BookingServiceCaller bookingsCaller;

    public void notify(EventCreatedMessage message) {
        var dto = formatter.format(message);
        sendToSubscribers(dto);
    }

    public void notify(EventDateChangedMessage message) {
        var dto = formatter.format(message);
        var emails = bookingsCaller.findCustomerEmailsByEventId(message.eventId()).block();
        if (emails == null) {
            return;
        }
        for (var email : emails) {
            sender.send(email, dto);
        }
    }

    public void notify(BookingConfirmedMessage message) {
        var dto = formatter.format(message);
        sender.send(message.customerEmail(), dto);
    }

    public void notify(BookingDeletedByAdminMessage message) {
        var dto = formatter.format(message);
        sender.send(message.customerEmail(), dto);
    }

    public void notify(BookingsCascadeCanceledMessage message) {
        for (var booking : message.bookings()) {
            var dto = formatter.formatCascadeCanceled(message.eventTitle(), booking);
            sender.send(booking.customerEmail(), dto);
        }
    }

    public void notify(BookingsOutcompetedMessage message) {
        for (var booking : message.bookings()) {
            var dto = formatter.formatOutcompeted(message.eventTitle(), booking);
            sender.send(booking.customerEmail(), dto);
        }
    }

    private void sendToSubscribers(EmailMessageDto messageDto) {
        var subscribers = settingsRepository.findAllByNotifyNewEventsTrue();
        for (var settings : subscribers) {
            sender.send(settings.getCustomerEmail(), messageDto);
        }
    }
}
