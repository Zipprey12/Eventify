package ru.zipprey.eventify.bot.command.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.bot.command.AsyncCommand;
import ru.zipprey.eventify.bot.message.caller.BookingServiceCaller;
import ru.zipprey.eventify.bot.message.caller.EventServiceCaller;
import ru.zipprey.eventify.bot.model.dto.BookingResponse;
import ru.zipprey.eventify.bot.model.dto.EventInfo;
import ru.zipprey.eventify.bot.repository.TelegramSubscriptionRepository;
import ru.zipprey.eventify.bot.service.sender.MessageSender;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BooksCommand extends AsyncCommand {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault());
    private static final String KEY = "/my_books";

    private final MessageSender sender;
    private final TelegramSubscriptionRepository subscriptionRepository;
    private final BookingServiceCaller bookingCaller;
    private final EventServiceCaller eventCaller;

    public BooksCommand(@Qualifier("botTaskExecutor") Executor executor,
                        MessageSender sender,
                        TelegramSubscriptionRepository subscriptionRepository,
                        BookingServiceCaller bookingCaller,
                        EventServiceCaller eventCaller) {
        super(executor, sender);
        this.sender = sender;
        this.subscriptionRepository = subscriptionRepository;
        this.bookingCaller = bookingCaller;
        this.eventCaller = eventCaller;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    protected void handle(long chatId, String[] args) {
        var subscription = subscriptionRepository.findById(chatId).orElse(null);
        if (subscription == null) {
            sender.sendText(chatId, "Аккаунт не привязан. Привяжите Telegram на сайте Eventify.");
            return;
        }

        var bookings = bookingCaller.getBookingsByEmail(subscription.getCustomerEmail());
        if (bookings.isEmpty()) {
            sender.sendText(chatId, "У вас пока нет бронирований");
            return;
        }

        var eventIds = bookings.stream().map(BookingResponse::eventId).distinct().toList();
        var eventsById = safeFindEvents(eventIds);

        sender.sendText(chatId, formatBookings(bookings, eventsById));
    }

    private Map<Long, EventInfo> safeFindEvents(List<Long> eventIds) {
        try {
            return eventCaller.findByIds(eventIds).stream()
                    .collect(Collectors.toMap(EventInfo::id, Function.identity()));
        } catch (Exception e) {
            return Map.of();
        }
    }

    private String formatBookings(List<BookingResponse> bookings, Map<Long, EventInfo> eventsById) {
        var sb = new StringBuilder("Ваши бронирования:\n\n");
        for (var b : bookings) {
            var event = eventsById.get(b.eventId());
            var title = event != null ? event.title() : "Событие удалено";
            var dateTime = event != null && event.dateTime() != null ? DATE_FORMAT.format(event.dateTime()) : "—";
            var status = b.confirmed() ? "подтверждено" : "ожидает подтверждения";

            sb.append("• ").append(title)
                    .append(" — ").append(dateTime)
                    .append("\nстатус: ").append(status)
                    .append(", билетов: ").append(b.ticketsCount())
                    .append("\n\n");
        }
        return sb.toString().strip();
    }
}