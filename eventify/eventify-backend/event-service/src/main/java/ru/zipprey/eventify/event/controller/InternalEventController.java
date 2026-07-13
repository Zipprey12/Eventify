package ru.zipprey.eventify.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.zipprey.eventify.event.service.EventService;
import ru.zipprey.eventify.eventapi.model.EventDto;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class InternalEventController {

    private final EventService eventService;

    @PostMapping("/batch")
    public List<EventDto> findByIds(@RequestBody List<Long> ids) {
        return eventService.findByIds(ids);
    }

    @PutMapping("/{id}/book")
    public EventDto bookTickets(@PathVariable Long id, @RequestParam int count) {
        return eventService.bookTickets(id, count);
    }

    @PutMapping("/{id}/free")
    public EventDto freeUpPlaces(@PathVariable Long id, @RequestParam int count) {
        return eventService.freeUpPlaces(id, count);
    }

}
