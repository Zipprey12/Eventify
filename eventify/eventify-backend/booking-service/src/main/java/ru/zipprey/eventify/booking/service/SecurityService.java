package ru.zipprey.eventify.booking.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SecurityService {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public boolean isAdmin(Authentication authentication){
        return authentication.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), ROLE_ADMIN));
    }

}
