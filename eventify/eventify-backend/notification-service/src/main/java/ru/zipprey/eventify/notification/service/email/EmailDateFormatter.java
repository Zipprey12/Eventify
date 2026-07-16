package ru.zipprey.eventify.notification.service.email;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class EmailDateFormatter {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter
            .ofPattern("dd.MM.yyyy HH:mm")
            .withZone(ZoneId.of("Europe/Moscow"));

    public static String format(Instant instant) {
        return FORMAT.format(instant);
    }
}
