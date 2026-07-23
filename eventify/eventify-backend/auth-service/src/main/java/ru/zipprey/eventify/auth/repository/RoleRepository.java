package ru.zipprey.eventify.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zipprey.eventify.auth.model.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

}
