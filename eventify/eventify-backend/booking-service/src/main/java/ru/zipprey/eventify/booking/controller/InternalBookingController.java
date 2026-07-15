package ru.zipprey.eventify.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.zipprey.eventify.booking.repository.BookingRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/bookings")
public class InternalBookingController {

    private final BookingRepository repository;

    @GetMapping("/emails")
    public List<String> getCustomerEmails(@RequestParam Long eventId) {
        return repository.findCustomerEmailsByEventId(eventId);
    }
}

