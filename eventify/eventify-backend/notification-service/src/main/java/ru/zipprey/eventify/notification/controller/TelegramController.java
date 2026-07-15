package ru.zipprey.eventify.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zipprey.eventify.notification.service.telegram.TelegramService;

@RestController
@RequestMapping("/user/telegram")
@RequiredArgsConstructor
public class TelegramController {

    private final TelegramService service;

    @PostMapping(value = "/link", produces = MediaType.TEXT_PLAIN_VALUE)
    public String link(Authentication authentication) {
        return service.generateLinkCode(authentication);
    }

}
