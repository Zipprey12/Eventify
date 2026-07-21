package ru.zipprey.eventify.notification.messaging.consumption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.kafka.event.EventCreatedMessage;
import ru.zipprey.eventify.kafka.event.EventDateChangedMessage;
import ru.zipprey.eventify.notification.messaging.consumption.deduplicate.KafkaDeduplicateService;
import ru.zipprey.eventify.notification.service.email.EmailService;
import ru.zipprey.eventify.notification.service.reminder.EventReminderService;

@Slf4j
@Component
public class EventMessageConsumer extends MessageConsumer {

    private static final String EVENT_CREATED = "event.created";
    private static final String EVENT_DATE_CHANGED = "event.date-changed";

    private final EventReminderService reminderService;

    public EventMessageConsumer(KafkaDeduplicateService deduplicateService,
                                EmailService emailService,
                                EventReminderService reminderService) {
        super(deduplicateService, emailService);
        this.reminderService = reminderService;
    }

    @KafkaListener(topics = EVENT_CREATED, groupId = "notification-service-event-created")
    public void handle(EventCreatedMessage message) {
        logMessage(EVENT_CREATED, message);

        var eventId = message.eventId();
        if (getDeduplicateService().isDuplicate(EVENT_CREATED, eventId)) {
            logDuplicate(EVENT_CREATED, eventId);
            return;
        }

        getEmailService().notify(message);
    }

    @KafkaListener(topics = EVENT_DATE_CHANGED, groupId = "notification-service-event-date-changed")
    public void handleDateChanged(EventDateChangedMessage message) {
        logMessage(EVENT_DATE_CHANGED, message);

        var operationId = message.operationId();
        if (getDeduplicateService().isDuplicate(EVENT_DATE_CHANGED, operationId)) {
            logDuplicate(EVENT_DATE_CHANGED, operationId);
            return;
        }

        getEmailService().notify(message);
        safeExecute(() -> reminderService.rescheduleForEventDateChange(message.eventId(), message.newDateTime()),
                "rescheduleForEventDateChange, eventId=" + message.eventId());
    }
}
