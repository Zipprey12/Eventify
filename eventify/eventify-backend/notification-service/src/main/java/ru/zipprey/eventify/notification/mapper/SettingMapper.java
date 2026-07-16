package ru.zipprey.eventify.notification.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.zipprey.eventify.notification.model.dto.SettingsDto;
import ru.zipprey.eventify.notification.model.enity.Settings;

@Mapper(componentModel = "spring")
public interface SettingMapper {

    SettingsDto toDto(Settings setting);

    @Mapping(target = "customerEmail", ignore = true)
    @Mapping(target = "emailConfirmed", ignore = true)
    @Mapping(target = "verificationCode", ignore = true)
    void updateEntity(SettingsDto dto, @MappingTarget Settings entity);

}
