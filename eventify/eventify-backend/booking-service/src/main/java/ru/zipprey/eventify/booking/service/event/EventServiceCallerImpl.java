package ru.zipprey.eventify.booking.service.event;

import org.springframework.stereotype.Service;
import ru.zipprey.eventify.eventapi.model.EventDto;

import java.util.List;
import java.util.Map;

@Service
public class EventServiceCallerImpl implements EventsServiceCaller {

    @Override
    public EventDto findById(long id) {
        return null;
    }

    @Override
    public Map<Long, EventDto> findByIds(List<Long> ids) {
        return Map.of();
    }

    @Override
    public void bookTickets(long eventId, int count) {
        //todo
    }

    @Override
    public void freeUpPlaces(long eventId, int count) {
        //todo
    }
}
