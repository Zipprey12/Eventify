package ru.zipprey.eventify.notification.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.zipprey.eventify.common.model.ErrorResponse;
import ru.zipprey.eventify.common.model.Level;

@RestControllerAdvice
public class NotificationExceptionHandler {

    @ExceptionHandler(SettingsNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleSettingsNotFoundException(SettingsNotFoundException e) {
        return new ErrorResponse("NOTIFICATION_SETTINGS_NOT_FOUND", Level.ERROR, e.getMessage(), null);
    }

}
