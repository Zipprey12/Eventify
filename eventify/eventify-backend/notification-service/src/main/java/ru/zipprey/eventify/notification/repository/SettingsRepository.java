package ru.zipprey.eventify.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zipprey.eventify.notification.model.enity.Settings;

import java.util.List;

public interface SettingsRepository extends JpaRepository<Settings, String> {

    List<Settings> findAllByNotifyNewEventsTrue();

}
