package ru.zipprey.eventify.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.zipprey.eventify.event.service.EventService;
import ru.zipprey.eventify.eventapi.model.EventDto;

import java.time.Instant;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class BaseController {

    private final EventService eventService;

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable Long id) {
        return eventService.findById(id);
    }

    @GetMapping
    public Page<EventDto> getEvents(@RequestParam(required = false) Instant from,
                                    @RequestParam(required = false) Instant to,
                                    Pageable pageable) {

        return eventService.findAll(pageable, from, to);
    }
}
