package ru.zipprey.eventify.bot.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import ru.zipprey.eventify.bot.message.caller.BookingServiceCaller;
import ru.zipprey.eventify.bot.message.caller.EventServiceCaller;
import ru.zipprey.eventify.bot.model.dto.ConfirmedBooking;
import ru.zipprey.eventify.bot.model.dto.EventInfo;
import ru.zipprey.eventify.bot.model.entity.TelegramSubscription;
import ru.zipprey.eventify.bot.repository.SentReminderRepository;
import ru.zipprey.eventify.bot.repository.TelegramSubscriptionRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueRefreshScheduler {

    private static final int UPCOMING_WINDOW_HOURS = 48;

    private final TelegramSubscriptionRepository subscriptionRepository;
    private final BookingServiceCaller bookingCaller;
    private final EventServiceCaller eventCaller;
    private final SentReminderRepository sentReminderRepository;
    private final ReminderQueue queue;

    @Scheduled(fixedDelayString = "${reminder.refresh-interval-ms}")
    public void refresh() {
        try {
            doRefresh();
        } catch (WebClientRequestException e) {
            log.error("Не удалось обновить очередь напоминаний. Сервис недоступен: {}",
                    e.getMessage());
        }
    }

    private void doRefresh() {
        var subscribed = subscriptionRepository.findAll();
        if (subscribed.isEmpty()) {
            return;
        }

        var byEmail = groupByEmail(subscribed);
        var confirmed = confirmedBookingsForSubscribers(byEmail);
        if (confirmed.isEmpty()) {
            return;
        }

        var eventsById = fetchEventsById(confirmed);
        var scheduled = scheduleReminders(confirmed, byEmail, eventsById);
        log.debug("Очередь напоминаний обновлена: подписанных:{}, подтверждённых броней в работе:{}, добавлено:{}",
                subscribed.size(), confirmed.size(), scheduled);
    }

    private Map<String, TelegramSubscription> groupByEmail(List<TelegramSubscription> subscribed) {
        return subscribed.stream()
                .collect(Collectors.toMap(TelegramSubscription::getCustomerEmail, s -> s, (a, b) -> a));
    }

    private List<ConfirmedBooking> confirmedBookingsForSubscribers(Map<String, TelegramSubscription> byEmail) {
        return bookingCaller.getConfirmed().stream()
                .filter(b -> byEmail.containsKey(b.email()))
                .toList();
    }

    private Map<Long, EventInfo> fetchEventsById(List<ConfirmedBooking> bookings) {
        var eventIds = bookings.stream().map(ConfirmedBooking::eventId).distinct().toList();
        return eventCaller.findByIds(eventIds).stream()
                .collect(Collectors.toMap(EventInfo::id, Function.identity()));
    }

    private int scheduleReminders(List<ConfirmedBooking> confirmed,
                                  Map<String, TelegramSubscription> byEmail,
                                  Map<Long, EventInfo> eventsById) {
        var now = Instant.now();
        var horizon = now.plus(UPCOMING_WINDOW_HOURS, ChronoUnit.HOURS);
        var scheduled = 0;

        for (var booking : confirmed) {
            var event = eventsById.get(booking.eventId());
            if (isEventDue(event, now, horizon)) {
                var subscriber = byEmail.get(booking.email());
                if (sentReminderRepository.existsByBookingIdAndChatId(booking.bookingId(), subscriber.getChatId())) {
                    continue;
                }

                var notifyAt = event.dateTime().minus(subscriber.getNotifyBeforeHours(), ChronoUnit.HOURS);
                queue.add(booking.bookingId(), subscriber.getChatId(), notifyAt, event.title(), event.description());
                scheduled++;
            }
        }
        return scheduled;
    }

    private boolean isEventDue(EventInfo event, Instant now, Instant horizon) {
        return event != null && event.dateTime() != null
                && event.dateTime().isAfter(now) && event.dateTime().isBefore(horizon);
    }
}