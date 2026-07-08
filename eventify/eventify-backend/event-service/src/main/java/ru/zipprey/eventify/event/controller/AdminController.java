package ru.zipprey.eventify.event.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.zipprey.eventify.event.model.dto.request.EventRequest;
import ru.zipprey.eventify.event.service.EventService;
import ru.zipprey.eventify.eventapi.model.EventDto;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminController {

    private final EventService eventService;

    @PutMapping("/{id}")
    public EventDto update(@PathVariable long id, @Valid @RequestBody EventRequest request) {
        return eventService.update(id, request);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEvent(@Valid @RequestBody EventRequest request) {
        return eventService.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long id) {
        eventService.delete(id);
    }

}
