package ru.zipprey.eventify.notification.model.dto;

public record TelegramLinkDto(
        String email,
        Integer notifyBeforeHours,
        boolean linked) {
}
