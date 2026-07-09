package ru.zipprey.eventify.notification.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.zipprey.eventify.notification.model.dto.SettingsDto;
import ru.zipprey.eventify.notification.model.enity.Settings;

@Mapper(componentModel = "spring")
public interface SettingMapper {

    SettingsDto toDto(Settings setting);

    @Mapping(target = "customerEmail", ignore = true)
    Settings toEntity(SettingsDto dto);

}
