package ru.zipprey.eventify.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.zipprey.eventify.notification.exception.SettingsNotFoundException;
import ru.zipprey.eventify.notification.exception.TelegramLinkNotFoundException;
import ru.zipprey.eventify.notification.model.dto.SubscribedUserDto;
import ru.zipprey.eventify.notification.model.dto.TelegramLinkDto;
import ru.zipprey.eventify.notification.model.dto.request.LinkRequest;
import ru.zipprey.eventify.notification.model.dto.request.NotificationTimeRequest;
import ru.zipprey.eventify.notification.model.enity.Settings;
import ru.zipprey.eventify.notification.model.enity.TelegramLink;
import ru.zipprey.eventify.notification.repository.SettingsRepository;
import ru.zipprey.eventify.notification.repository.telegram.TelegramLinkRepository;
import ru.zipprey.eventify.notification.service.telegram.TelegramService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal/telegram")
@RequiredArgsConstructor
public class TelegramInternalController {

    private final TelegramLinkRepository telegramLinkRepository;
    private final SettingsRepository settingsRepository;
    private final TelegramService telegramService;

    @PostMapping("/link")
    public String link(@RequestBody LinkRequest request) {
        return telegramService.consumeLinkCode(request.chatId(), request.code());
    }

    @GetMapping("/{chatId}")
    public TelegramLinkDto findByChatId(@PathVariable long chatId) {
        var link = telegramLinkRepository.findByChatId(chatId)
                .orElseThrow(() -> new TelegramLinkNotFoundException(chatId));

        var hours = settingsRepository.findById(link.getCustomerEmail())
                .map(Settings::getNotifyBeforeHours)
                .orElse(null);

        return new TelegramLinkDto(link.getCustomerEmail(), hours, true);
    }

    @PutMapping("/{chatId}/notification-time")
    public void updateNotificationTime(@PathVariable long chatId,
                                       @RequestBody NotificationTimeRequest request) {
        var link = telegramLinkRepository.findByChatId(chatId)
                .orElseThrow(() -> new TelegramLinkNotFoundException(chatId));

        var settings = settingsRepository.findById(link.getCustomerEmail())
                .orElseThrow(() -> new SettingsNotFoundException(link.getCustomerEmail()));

        settings.setNotifyBeforeHours(request.hours());
        settingsRepository.save(settings);
    }

    @DeleteMapping("/{chatId}")
    public void unlink(@PathVariable long chatId) {
        telegramService.unlink(chatId);
    }

    @GetMapping("/subscribed")
    public List<SubscribedUserDto> getSubscribed() {
        var links = telegramLinkRepository.findAllByChatIdIsNotNull();
        if (links.isEmpty()) {
            return List.of();
        }

        var emails = links.stream().map(TelegramLink::getCustomerEmail).toList();
        Map<String, Settings> settingsByEmail = settingsRepository.findAllById(emails).stream()
                .collect(Collectors.toMap(Settings::getCustomerEmail, Function.identity()));

        return links.stream()
                .map(link -> {
                    var settings = settingsByEmail.get(link.getCustomerEmail());
                    if (settings == null
                            || !Boolean.TRUE.equals(settings.getNotifyUpcoming())
                            || settings.getNotifyBeforeHours() == null) {
                        return null;
                    }
                    return new SubscribedUserDto(link.getChatId(), link.getCustomerEmail(), settings.getNotifyBeforeHours());
                })
                .filter(Objects::nonNull)
                .toList();
    }
}