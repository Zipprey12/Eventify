package ru.zipprey.eventify.booking.messaging.consumption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.booking.messaging.consumption.deduplicate.KafkaDeduplicateService;
import ru.zipprey.eventify.kafka.event.EventDeletedMessage;
import ru.zipprey.eventify.kafka.event.EventOverbookedMessage;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventMessageConsumer {

    private static final String DUPLICATE_LOG = "Дубль {}: operationId={},  eventId={}, пропуск операции";

    public static final String EVENT_OVERBOOKED = "event.overbooked";
    public static final String EVENT_DELETED = "event.deleted";

    private final EventMessageHandler handler;
    private final KafkaDeduplicateService deduplicateService;

    @KafkaListener(topics = EVENT_OVERBOOKED, groupId = "booking-service-event-overbooked")
    public void handle(EventOverbookedMessage message) {
        logConsumption(EVENT_OVERBOOKED, message);

        var operationId = message.operationId();
        var eventId = message.eventId();
        if (deduplicateService.isDuplicate(EVENT_OVERBOOKED, operationId)) {
            log.info(DUPLICATE_LOG, EVENT_OVERBOOKED, eventId, operationId);
            return;
        }

        handler.handle(message);
    }

    @KafkaListener(topics = EVENT_DELETED, groupId = "booking-service-event-deleted")
    public void handle(EventDeletedMessage message) {
        logConsumption(EVENT_DELETED, message);

        var eventId = message.eventId();
        if (deduplicateService.isDuplicate(EVENT_DELETED, eventId)) {
            log.info("Дубль {}: eventId={}, пропуск операции", EVENT_DELETED, eventId);
            return;
        }

        handler.handle(message);
    }

    private void logConsumption(String topic, Object message) {
        log.info("Получено {} : {}", topic, message);
    }
}