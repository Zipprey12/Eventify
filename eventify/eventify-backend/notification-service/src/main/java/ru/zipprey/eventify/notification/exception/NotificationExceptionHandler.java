package ru.zipprey.eventify.notification.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.zipprey.eventify.common.model.ErrorResponse;
import ru.zipprey.eventify.common.model.Level;

@Slf4j
@RestControllerAdvice
public class NotificationExceptionHandler {

    @ExceptionHandler(SettingsNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleSettingsNotFoundException(SettingsNotFoundException e) {
        log.error(e.getMessage());
        return new ErrorResponse("NOTIFICATION_SETTINGS_NOT_FOUND", Level.ERROR, e.getMessage(), null);
    }

    @ExceptionHandler(TelegramLinkNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTelegramLinkNotFoundException(TelegramLinkNotFoundException e) {
        log.error(e.getMessage());
        return new ErrorResponse("TELEGRAM_LINK_NOT_FOUND", Level.ERROR, e.getMessage(), null);
    }

    @ExceptionHandler(EmailConfirmException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEmailConfirmException(EmailConfirmException e) {
        log.error(e.getMessage());
        return new ErrorResponse("EMAIL_CONFIRMATION_EXCEPTION", Level.ERROR, e.getMessage(), null);
    }

    @ExceptionHandler(TelegramInvalidCodeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTelegramCodeInvalidException(TelegramInvalidCodeException e) {
        log.error(e.getMessage());
        return new ErrorResponse("TELEGRAM_CODE_INVALID", Level.ERROR, e.getMessage(), null);
    }

    @ExceptionHandler(TelegramLinkedToAnotherUserException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleTelegramLinkedToAnotherUserException(TelegramLinkedToAnotherUserException e) {
        log.error(e.getMessage());
        return new ErrorResponse("TELEGRAM_LINKED_TO_ANOTHER_USER", Level.ERROR,
                e.getMessage(), null);
    }
}