package ru.zipprey.eventify.notification.messaging.consumption.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.kafka.event.EventCreatedMessage;
import ru.zipprey.eventify.kafka.event.EventDateChangedMessage;
import ru.zipprey.eventify.notification.messaging.consumption.MessageConsumer;
import ru.zipprey.eventify.notification.messaging.consumption.deduplicate.KafkaDeduplicateService;

@Slf4j
@Component
public class EventMessageConsumer extends MessageConsumer {

    private static final String EVENT_CREATED = "event.created";
    private static final String EVENT_DATE_CHANGED = "event.date-changed";

    private final EventMessageHandler handler;

    public EventMessageConsumer(KafkaDeduplicateService deduplicateService, EventMessageHandler handler) {
        super(deduplicateService);
        this.handler = handler;
    }

    @KafkaListener(topics = EVENT_CREATED, groupId = "notification-service-event-created")
    public void handle(EventCreatedMessage message) {
        logConsumption(EVENT_CREATED, message);

        var eventId = message.eventId();
        if (getDeduplicateService().isDuplicate(EVENT_CREATED, eventId)) {
            logDuplicate(EVENT_CREATED, eventId);
            return;
        }

        handler.handleCreated(message);
    }

    @KafkaListener(topics = EVENT_DATE_CHANGED, groupId = "notification-service-event-date-changed")
    public void handleDateChanged(EventDateChangedMessage message) {
        logConsumption(EVENT_DATE_CHANGED, message);

        var operationId = message.operationId();
        if (getDeduplicateService().isDuplicate(EVENT_DATE_CHANGED, operationId)) {
            logDuplicate(EVENT_DATE_CHANGED, operationId);
            return;
        }

        handler.handleDateChanged(message);
    }
}
