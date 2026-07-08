package ru.zipprey.eventify.auth.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.zipprey.eventify.auth.model.dto.UserDto;
import ru.zipprey.eventify.auth.model.entity.User;
import ru.zipprey.eventify.auth.model.security.UserPrincipal;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", source = "role.name")
    @Mapping(target = "authorities", ignore = true)
    UserPrincipal toPrincipal(User user);

    @Mapping(target = "role", source = "role.name")
    UserDto toDto(User user);
}
