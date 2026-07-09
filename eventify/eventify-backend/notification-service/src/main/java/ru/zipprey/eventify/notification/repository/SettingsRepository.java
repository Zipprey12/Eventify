package ru.zipprey.eventify.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zipprey.eventify.notification.model.enity.Settings;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, String> {

}
