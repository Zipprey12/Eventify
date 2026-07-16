package ru.zipprey.eventify.notification.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.zipprey.eventify.kafka.booking.BookingConfirmedMessage;
import ru.zipprey.eventify.notification.model.enity.EventReminder;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface EventReminderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sent", constant = "false")
    @Mapping(target = "sentAt", ignore = true)
    EventReminder toEntity(BookingConfirmedMessage message, Instant remindAt, Integer notifyBeforeHours);
}
