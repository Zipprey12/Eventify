package ru.zipprey.eventify.auth.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {

    @Email
    @Size(min = 8, max = 70)
    private String email;

    private String role;
}
