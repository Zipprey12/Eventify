package ru.zipprey.eventify.common.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.zipprey.eventify.common.model.ErrorDetail;
import ru.zipprey.eventify.common.model.ErrorResponse;
import ru.zipprey.eventify.common.model.Level;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException e) {
        var details = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorDetail(fe.getField(), fe.getDefaultMessage()))
                .toList();

        return new ErrorResponse("VALIDATION_FAILED", Level.ERROR,
                "Некоторые поля заполнены неверно", details);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpected(Exception e) {
        log.error("Необработанная ошибка", e);
        return new ErrorResponse("INTERNAL_ERROR", Level.ERROR, "Внутренняя ошибка сервера", null);
    }
}
