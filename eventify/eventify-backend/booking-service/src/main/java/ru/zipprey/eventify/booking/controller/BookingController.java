package ru.zipprey.eventify.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.zipprey.eventify.booking.model.dto.BookingResponse;
import ru.zipprey.eventify.booking.model.dto.request.CreateBookingRequest;
import ru.zipprey.eventify.booking.model.dto.request.UpdateBookingRequest;
import ru.zipprey.eventify.booking.service.booking.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService service;

    @GetMapping("/{id}")
    public BookingResponse getById(@PathVariable Long id, Authentication authentication) {
        return service.getById(id, authentication);
    }

    @GetMapping
    public List<BookingResponse> getAll(Authentication authentication){
        return service.getAll(authentication);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(@RequestBody CreateBookingRequest request, Authentication authentication){
        return service.save(request, authentication);
    }

    @PutMapping("/{id}")
    public BookingResponse updateById(@PathVariable Long id, @RequestBody UpdateBookingRequest request, Authentication authentication) {
        return service.update(id, request, authentication);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id, Authentication authentication){
        service.delete(id, authentication);
    }
}
