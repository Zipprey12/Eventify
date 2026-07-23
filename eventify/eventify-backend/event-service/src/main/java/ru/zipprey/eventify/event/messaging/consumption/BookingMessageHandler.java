package ru.zipprey.eventify.event.messaging.consumption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.zipprey.eventify.event.service.EventService;
import ru.zipprey.eventify.kafka.booking.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingMessageHandler {

    private final EventService eventService;

    public void handle(CancellationFailedMessage message) {
        var id = message.eventId();
        var count = message.requiredTickets();
        eventService.addTotalTickets(id, count);
        log.info("Не удалось отменить брони. К событию {} добавлено {} билетов", id, count);
    }

    public void handle(BookingsOutcompetedMessage message) {
        var eventId = message.eventId();
        var difference = message.freedTickets() - message.requiredTickets();
        if (difference > 0) {
            eventService.freeUpPlaces(eventId, difference);
            log.info("Освобождено {} билетов для event: {}", difference, eventId);
        }
    }

    public void handle(BookingDeletedMessage message) {
        if (!Boolean.TRUE.equals(message.wasConfirmed())) {
            return;
        }
        log.info("Освобождение мест: eventId={}, count={}", message.eventId(), message.ticketsCount());
        eventService.freeUpPlaces(message.eventId(), message.ticketsCount());
    }

    public void handle(BookingDeletedByAdminMessage message) {
        if (!Boolean.TRUE.equals(message.wasConfirmed())) {
            return;
        }
        log.info("Освобождение мест (admin): eventId={}, count={}", message.eventId(), message.ticketsCount());
        eventService.freeUpPlaces(message.eventId(), message.ticketsCount());
    }

    public void handle(BookTicketsMessage message) {
        log.info("Бронирование мест: eventId={}, count={}", message.eventId(), message.ticketsCount());
        eventService.bookTickets(message.eventId(), message.ticketsCount());
    }
}