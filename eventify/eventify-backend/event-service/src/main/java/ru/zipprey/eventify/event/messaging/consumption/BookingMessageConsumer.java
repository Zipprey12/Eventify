package ru.zipprey.eventify.event.messaging.consumption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.event.messaging.consumption.deduplicate.KafkaDeduplicateService;
import ru.zipprey.eventify.event.service.EventService;
import ru.zipprey.eventify.kafka.booking.BookingsCanceledMessage;
import ru.zipprey.eventify.kafka.booking.CancellationFailedMessage;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookingMessageConsumer {

    public static final String FORCE_CANCELLATION_ERROR = "booking.force-cancellation-error";
    public static final String DUPLICATE_LOG = "Дубль {}. Пропуск операции: {} для event: {}";
    public static final String ADDITION_MESSAGE = "Не удалось отменить брони. К событию {} добавлено {} билетов";
    public static final String FREE_UP_TICKETS = "Освобождено {} билетов для event: {}";
    private static final String FORCE_CANCELLED = "booking.force-canceled";
    private final KafkaDeduplicateService kafkaDeduplicateService;
    private final EventService eventService;

    @KafkaListener(topics = FORCE_CANCELLATION_ERROR, groupId = "event-service-booking-force-cancellation-error")
    public void handle(CancellationFailedMessage message) {
        logMessageConsumption(FORCE_CANCELLATION_ERROR);

        if (kafkaDeduplicateService.isDuplicate(FORCE_CANCELLATION_ERROR, message.operationId())) {
            logDuplicate(FORCE_CANCELLATION_ERROR, message.operationId(), message.eventId());
            return;
        }

        var id = message.eventId();
        var count = message.requiredTickets();
        eventService.addTotalTickets(message.eventId(), count);
        log.info(ADDITION_MESSAGE, id, count);
    }

    @KafkaListener(topics = FORCE_CANCELLED, groupId = "event-service-booking-force-canceled")
    public void handle(BookingsCanceledMessage message) {
        logMessageConsumption(FORCE_CANCELLED);

        var operationId = message.operationId();
        var eventId = message.eventId();

        if (kafkaDeduplicateService.isDuplicate(FORCE_CANCELLED, operationId)) {
            logDuplicate(FORCE_CANCELLED, operationId, eventId);
            return;
        }

        var difference = message.freedTickets() - message.requiredTickets();
        if (difference > 0) {
            eventService.freeUpPlaces(eventId, difference);
            log.info(FREE_UP_TICKETS, difference, eventId);
        }
    }

    private void logMessageConsumption(String message) {
        log.info("Получено: {}", message);
    }

    private void logDuplicate(String message, UUID operationId, long eventId) {
        log.info(DUPLICATE_LOG, message, operationId, eventId);
    }
}
