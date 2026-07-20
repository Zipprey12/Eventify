package ru.zipprey.eventify.notification.model.dto;

public record SubscribedUserDto(
        Long chatId,
        String email,
        Integer notifyBeforeHours) {
}
