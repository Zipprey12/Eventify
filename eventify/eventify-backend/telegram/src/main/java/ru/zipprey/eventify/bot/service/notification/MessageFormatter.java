package ru.zipprey.eventify.bot.service.notification;

import lombok.experimental.UtilityClass;
import ru.zipprey.eventify.kafka.booking.BookingConfirmedMessage;
import ru.zipprey.eventify.kafka.booking.BookingDeletedByAdminMessage;
import ru.zipprey.eventify.kafka.booking.CanceledBookingEntry;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class MessageFormatter {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault());

    private static final String TEMPLATE_CONFIRMED = """
            Бронь подтверждена: %s
            Дата: %s
            Билетов: %d
            """;

    private static final String TEMPLATE_DELETED_BY_ADMIN = """
            Бронь отменена администратором: %s
            Билетов: %d
            
            Приносим извинения за неудобства.
            """;

    private static final String TEMPLATE_CANCELED = """
            %s
            Билетов: %d
            
            Приносим извинения за неудобства.
            """;

    public static String format(BookingConfirmedMessage message) {
        return String.format(
                TEMPLATE_CONFIRMED,
                message.eventTitle(),
                format(message.eventDateTime()),
                message.ticketsCount()
        );
    }

    public static String format(BookingDeletedByAdminMessage message) {
        return String.format(
                TEMPLATE_DELETED_BY_ADMIN,
                message.eventTitle(),
                message.ticketsCount()
        );
    }

    public static String format(CanceledBookingEntry entry, String header) {
        return String.format(
                TEMPLATE_CANCELED,
                header,
                entry.ticketsCount()
        );
    }

    private static String format(Instant instant) {
        return DATE_FORMAT.format(instant);
    }
}
