package ru.zipprey.eventify.bot.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zipprey.eventify.bot.repository.TelegramSubscriptionRepository;
import ru.zipprey.eventify.bot.service.sender.MessageSender;
import ru.zipprey.eventify.kafka.booking.*;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MessageSender sender;
    private final TelegramSubscriptionRepository subscriptionRepository;

    public void notify(BookingConfirmedMessage message) {
        notify(message.customerEmail(), MessageFormatter.format(message));
    }

    public void notify(BookingDeletedByAdminMessage message) {
        notify(message.customerEmail(), MessageFormatter.format(message));
    }

    public void notify(BookingsCascadeCanceledMessage message) {
        for (var booking : message.bookings()) {
            notifyCanceledEntry(booking, "Бронь отменена: событие \"" + message.eventTitle() + "\" удалено");
        }
    }

    public void notify(BookingsOutcompetedMessage message) {
        for (var booking : message.bookings()) {
            notifyCanceledEntry(booking, "Бронь отменена: сократилось количество мест на \"" +
                    message.eventTitle() + "\"");
        }
    }

    public void notify(String email, String text) {
        subscriptionRepository.findByCustomerEmail(email)
                .ifPresent(subscription -> sender.sendText(subscription.getChatId(), text));
    }

    private void notifyCanceledEntry(CanceledBookingEntry booking, String header) {
        notify(booking.customerEmail(), MessageFormatter.format(booking, header));
    }
}
