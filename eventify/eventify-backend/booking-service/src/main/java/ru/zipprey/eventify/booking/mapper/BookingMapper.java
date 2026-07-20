package ru.zipprey.eventify.booking.mapper;

import org.mapstruct.*;
import ru.zipprey.eventify.booking.model.dto.BookingResponse;
import ru.zipprey.eventify.booking.model.dto.request.CreateBookingRequest;
import ru.zipprey.eventify.booking.model.dto.request.UpdateBookingRequest;
import ru.zipprey.eventify.booking.model.entity.Booking;
import ru.zipprey.eventify.eventapi.model.EventDto;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(source = "booking.id", target = "id")
    @Mapping(source = "eventDto", target = "event")
    @Mapping(source = "booking.ticketsCount", target = "ticketsCount")
    BookingResponse toResponse(Booking booking, EventDto eventDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateBookingRequest request, @MappingTarget Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "confirmed", constant = "false")
    @Mapping(target = "expiryTime", ignore = true)
    @Mapping(target = "ticketsCount", source = "request.ticketsCount")
    Booking toEntity(CreateBookingRequest request, String customerEmail);

    default BookingResponse toResponse(Booking booking) {
        var eventDto = new EventDto();
        eventDto.setId(booking.getEventId());
        return toResponse(booking, eventDto);
    }
}