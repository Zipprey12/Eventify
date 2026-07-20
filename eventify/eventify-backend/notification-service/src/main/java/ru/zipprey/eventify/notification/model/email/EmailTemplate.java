package ru.zipprey.eventify.notification.model.email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public enum EmailTemplate {

    EVENT_CREATED(
            "Новое событие: {title}",
            """
                    Здравствуйте!
                    
                    Появилось новое событие, которое может быть вам интересно:
                    
                    {title}
                    {description}
                    
                    Дата: {dateTime}
                    """,
            true
    ),

    EVENT_DATE_CHANGED(
            "Изменилась дата события: {eventTitle}",
            """
                    Здравствуйте!
                    
                    У события "{eventTitle}", на которое вы бронировали билеты, изменилась дата проведения.
                    Новая дата: {newDateTime}
                    """,
            true
    ),

    BOOKING_CONFIRMED(
            "Бронь подтверждена: {eventTitle}",
            """
                    Здравствуйте!
                    
                    Ваша бронь на событие "{eventTitle}" подтверждена.
                    
                    Количество билетов: {ticketsCount}
                    Дата события: {eventDateTime}
                    """,
            true
    ),

    BOOKING_DELETED_BY_ADMIN(
            "Бронь отменена администратором: {eventTitle}",
            """
                    Здравствуйте!
                    
                    Ваша бронь на событие "{eventTitle}" была отменена администратором.
                    
                    Количество билетов: {ticketsCount}
                    Дата события: {eventDateTime}
                    
                    Приносим извинения за неудобства.
                    """,
            true
    ),

    BOOKING_OUTCOMPETED(
            "Бронь отменена: {eventTitle}",
            """
                    Здравствуйте!
                    
                    К сожалению, мест на событие "{eventTitle}" стало меньше, и вашу бронь
                    ({ticketsCount} билет(ов)) не удалось сохранить.
                    
                    Приносим извинения за неудобства.
                    """,
            true
    ),

    BOOKING_CASCADE_CANCELED(
            "Бронь отменена: событие \"{eventTitle}\" удалено",
            """
                    Здравствуйте!
                    
                    Событие "{eventTitle}" было отменено организатором, поэтому ваша бронь
                    ({ticketsCount} билет(ов)) отменена автоматически.
                    
                    Приносим извинения за неудобства.
                    """,
            true
    ),

    EVENT_REMINDER(
            "Напоминание: {eventTitle}",
            """
                    Здравствуйте!
                    
                    Напоминаем, что скоро начнётся событие "{eventTitle}", на которое у вас забронированы билеты.
                    
                    Дата и время: {eventDateTime}
                    Количество билетов: {ticketsCount}
                    """,
            true
    ),

    EMAIL_CONFIRMATION(
            "Подтвердите email для получения уведомлений",
            """
                    Здравствуйте!
                    
                    Чтобы получать уведомления о бронированиях и событиях на этот email,
                    подтвердите его, перейдя по ссылке:
                    
                    {link}
                    
                    Если вы не регистрировались в Eventify — просто проигнорируйте это письмо.
                    """,
            false
    );

    private final String subjectTemplate;
    private final String bodyTemplate;

    @Getter
    private final boolean requiresConfirmation;

    private static String substituteValues(String template, Map<String, String> values) {
        var result = template;
        for (var entry : values.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }

    public EmailMessageDto generate(Map<String, String> args) {
        return new EmailMessageDto(substituteValues(subjectTemplate, args), substituteValues(bodyTemplate, args));
    }
}
