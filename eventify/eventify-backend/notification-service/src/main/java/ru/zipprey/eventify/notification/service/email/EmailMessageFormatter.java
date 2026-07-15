package ru.zipprey.eventify.notification.service.email;

import org.springframework.stereotype.Component;
import ru.zipprey.eventify.kafka.booking.BookingConfirmedMessage;
import ru.zipprey.eventify.kafka.booking.BookingDeletedByAdminMessage;
import ru.zipprey.eventify.kafka.booking.CanceledBookingEntry;
import ru.zipprey.eventify.kafka.event.EventCreatedMessage;
import ru.zipprey.eventify.kafka.event.EventDateChangedMessage;
import ru.zipprey.eventify.notification.model.email.EmailMessageDto;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class EmailMessageFormatter {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("dd.MM.yyyy HH:mm")
            .withZone(ZoneId.of("Europe/Moscow"));

    public EmailMessageDto format(EventCreatedMessage message) {
        var subject = "Новое событие: " + message.title();
        var body = """
                Здравствуйте!
                
                Появилось новое событие, которое может быть вам интересно:
                
                %s
                %s
                
                Дата: %s
                """.formatted(message.title(), message.description(), DATE_FORMAT.format(message.dateTime()));

        return new EmailMessageDto(subject, body);
    }

    //Todo - в событии нет email-ов тех, кто бронировал событие
    public EmailMessageDto format(EventDateChangedMessage message) {
        var subject = "Изменилась дата события: " + message.eventTitle();
        var body = """
                Здравствуйте!
                
                У события "%s", на которое вы бронировали билеты, изменилась дата проведения.
                Новая дата: %s
                """.formatted(message.eventTitle(), DATE_FORMAT.format(message.newDateTime()));

        return new EmailMessageDto(subject, body);
    }

    public EmailMessageDto format(BookingConfirmedMessage message) {
        var subject = "Бронь подтверждена: " + message.eventTitle();
        var body = """
                Здравствуйте!
                
                Ваша бронь на событие "%s" подтверждена.
                
                Количество билетов: %d
                Дата события: %s
                """.formatted(message.eventTitle(), message.ticketsCount(), DATE_FORMAT.format(message.eventDateTime()));

        return new EmailMessageDto(subject, body);
    }

    public EmailMessageDto format(BookingDeletedByAdminMessage message) {
        var subject = "Бронь отменена администратором: " + message.eventTitle();
        var body = """
                Здравствуйте!
                
                Ваша бронь на событие "%s" была отменена администратором.
                
                Количество билетов: %d
                Дата события: %s
                
                Приносим извинения за неудобства.
                """.formatted(message.eventTitle(), message.ticketsCount(), DATE_FORMAT.format(message.eventDateTime()));

        return new EmailMessageDto(subject, body);
    }

    public EmailMessageDto formatOutcompeted(String eventTitle, CanceledBookingEntry entry) {
        var subject = "Бронь отменена: " + eventTitle;
        var body = """
                Здравствуйте!
                
                К сожалению, мест на событие "%s" стало меньше, и вашу бронь
                (%d билет(ов)) не удалось сохранить.
                
                Приносим извинения за неудобства.
                """.formatted(eventTitle, entry.ticketsCount());
        return new EmailMessageDto(subject, body);
    }


    public EmailMessageDto formatCascadeCanceled(String eventTitle, CanceledBookingEntry booking) {
        var subject = "Бронь отменена: событие \"" + eventTitle + "\" удалено";
        var body = """
                Здравствуйте!
                
                Событие "%s" было отменено организатором, поэтому ваша бронь
                (%d билет(ов)) отменена автоматически.
                
                Приносим извинения за неудобства.
                """.formatted(eventTitle, booking.ticketsCount());
        return new EmailMessageDto(subject, body);
    }
}


