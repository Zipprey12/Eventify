package ru.zipprey.eventify.auth.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserDto(
        @Email @Size(min = 8, max = 70) String email,
        String role
) {
}
