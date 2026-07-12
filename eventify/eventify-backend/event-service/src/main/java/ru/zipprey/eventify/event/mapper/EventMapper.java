package ru.zipprey.eventify.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.zipprey.eventify.event.model.dto.request.EventRequest;
import ru.zipprey.eventify.event.model.entity.Event;
import ru.zipprey.eventify.eventapi.model.EventDto;
import ru.zipprey.eventify.kafka.event.EventCreatedMessage;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "date", target = "dateTime")
    @Mapping(source = "imageUrl", target = "coverUrl")
    EventDto toDto(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "availableTickets", ignore = true)
    @Mapping(source = "dateTime", target = "date")
    @Mapping(source = "coverUrl", target = "imageUrl")
    Event toEntity(EventRequest request);

    @Mapping(source = "dateTime", target = "date")
    @Mapping(source = "coverUrl", target = "imageUrl")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "totalTickets", ignore = true)
    @Mapping(target = "availableTickets", ignore = true)
    void updateEntity(EventRequest request, @MappingTarget Event existing);

    @Mapping(source = "id", target = "eventId")
    @Mapping(source = "date", target = "dateTime")
    EventCreatedMessage toCreateMessage(Event event);
}
