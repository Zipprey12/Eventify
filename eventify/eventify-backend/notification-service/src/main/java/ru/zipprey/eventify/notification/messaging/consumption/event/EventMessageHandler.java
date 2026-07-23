package ru.zipprey.eventify.notification.messaging.consumption.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.zipprey.eventify.kafka.event.EventCreatedMessage;
import ru.zipprey.eventify.kafka.event.EventDateChangedMessage;
import ru.zipprey.eventify.notification.messaging.consumption.MessageHandler;
import ru.zipprey.eventify.notification.service.email.EmailService;
import ru.zipprey.eventify.notification.service.reminder.EventReminderService;

@Slf4j
@Service
public class EventMessageHandler extends MessageHandler {

    public EventMessageHandler(EmailService emailService, EventReminderService reminderService) {
        super(emailService, reminderService);
    }

    public void handleCreated(EventCreatedMessage message) {
        getEmailService().notify(message);
    }

    public void handleDateChanged(EventDateChangedMessage message) {
        getEmailService().notify(message);
        safeExecute(() -> getReminderService().rescheduleForEventDateChange(message.eventId(), message.newDateTime()),
                "rescheduleForEventDateChange, eventId=" + message.eventId());
    }
}