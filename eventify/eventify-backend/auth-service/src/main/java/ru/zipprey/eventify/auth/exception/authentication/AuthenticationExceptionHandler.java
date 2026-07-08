package ru.zipprey.eventify.auth.exception.authentication;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.zipprey.eventify.common.model.ErrorResponse;
import ru.zipprey.eventify.common.model.Level;

@RestControllerAdvice
public class AuthenticationExceptionHandler {

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyExistException(UserAlreadyExistException e) {
        return new ErrorResponse("USER_ALREADY_EXISTS", Level.ERROR, e.getMessage(), null);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationException(AuthenticationException e) {
        return new ErrorResponse("INVALID_CREDENTIALS", Level.ERROR, "Неверный email или пароль", null);
    }
}
