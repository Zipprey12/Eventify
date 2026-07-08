package ru.zipprey.eventify.booking.service.event;

import ru.zipprey.eventify.eventapi.model.EventDto;

import java.util.List;
import java.util.Map;

public interface EventsServiceCaller {

    EventDto findById(long id);

    Map<Long, EventDto> findByIds(List<Long> ids);

    void bookTickets(long eventId, int count);

    void freeUpPlaces(long eventId, int count);
}
