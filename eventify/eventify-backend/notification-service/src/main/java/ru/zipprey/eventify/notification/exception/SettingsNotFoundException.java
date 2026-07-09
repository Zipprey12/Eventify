package ru.zipprey.eventify.notification.exception;

public class SettingsNotFoundException extends RuntimeException {

    public SettingsNotFoundException(String email) {
        super("Настройки уведомлений для " + email + " не найдены");
    }
}
