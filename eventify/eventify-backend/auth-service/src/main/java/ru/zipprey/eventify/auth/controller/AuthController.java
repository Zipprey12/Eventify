package ru.zipprey.eventify.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import ru.zipprey.eventify.auth.model.dto.request.LoginRequest;
import ru.zipprey.eventify.auth.model.dto.request.RegisterRequest;
import ru.zipprey.eventify.auth.model.dto.response.AuthResponse;
import ru.zipprey.eventify.auth.model.security.UserPrincipal;
import ru.zipprey.eventify.auth.services.UserService;
import ru.zipprey.eventify.common.security.JwtService;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        var userDto = userService.register(request);
        var token = jwtService.generateToken(userDto.getEmail(), userDto.getRole());

        log.info("Запрос на регистрацию пользователя: {}", request.getEmail());
        return new AuthResponse(token, userDto.getRole());
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            var principal = (UserPrincipal) authentication.getPrincipal();
            var token = jwtService.generateToken(Objects.requireNonNull(principal).getUsername(), principal.getRole());

            log.info("Успешный вход: {}", request.getEmail());
            return new AuthResponse(token, principal.getRole());
        } catch (AuthenticationException e) {
            log.warn("Неудачный вход: {}", request.getEmail());
            throw e;
        }
    }
}