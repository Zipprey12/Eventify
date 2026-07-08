package ru.zipprey.eventify.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.zipprey.eventify.booking.model.dto.BookingResponse;
import ru.zipprey.eventify.booking.service.booking.AdminBookingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/bookings")
public class AdminBookingController {

    private final AdminBookingService service;

    @GetMapping
    public Page<BookingResponse> getAll(
            @RequestParam(required = false) Long eventId,
            @RequestParam(defaultValue = "false") boolean unconfirmedOnly,
            Pageable pageable) {
        return service.getAll(eventId, unconfirmedOnly, pageable);
    }

    @PutMapping("/{id}/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirm(@PathVariable long id) {
        service.confirm(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}
