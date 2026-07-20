package ru.zipprey.eventify.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.zipprey.eventify.booking.model.dto.BookingInfo;
import ru.zipprey.eventify.booking.model.dto.ConfirmedBookingInfo;
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

    @GetMapping
    public List<BookingInfo> getBookingsByEmail(@RequestParam String email) {
        return repository.findAllByCustomerEmail(email).stream()
                .map(b -> new BookingInfo(
                        b.getId(),
                        b.getEventId(),
                        Boolean.TRUE.equals(b.getConfirmed()),
                        b.getTicketsCount()
                ))
                .toList();
    }

    @GetMapping("/confirmed")
    public List<ConfirmedBookingInfo> getConfirmed() {
        return repository.findAllByConfirmedTrue().stream()
                .map(b -> new ConfirmedBookingInfo(
                        b.getId(),
                        b.getEventId(),
                        b.getCustomerEmail(),
                        b.getTicketsCount()
                ))
                .toList();
    }
}