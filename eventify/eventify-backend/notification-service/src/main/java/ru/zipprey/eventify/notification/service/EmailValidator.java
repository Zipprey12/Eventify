package ru.zipprey.eventify.notification.service;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;

@UtilityClass
public class EmailValidator {

    public static String getEmail(Authentication authentication) {
        var email = authentication.getName();
        return validate(email);
    }

    public static String validate(String email) {
        return email.trim().toLowerCase();
    }
}
