package ru.zipprey.eventify.bot.message.caller;

import ru.zipprey.eventify.bot.model.dto.BookingResponse;
import ru.zipprey.eventify.bot.model.dto.ConfirmedBooking;

import java.util.List;

public interface BookingServiceCaller {

    List<BookingResponse> getBookingsByEmail(String email);

    List<ConfirmedBooking> getConfirmed();
}