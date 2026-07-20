package ru.zipprey.eventify.bot.model.dto;

public record DueReminder(
        long bookingId,
        long chatId,
        String eventTitle,
        String eventDescription) {
}
