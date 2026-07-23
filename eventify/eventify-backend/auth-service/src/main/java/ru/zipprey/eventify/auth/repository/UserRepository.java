package ru.zipprey.eventify.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zipprey.eventify.auth.model.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String userName);

}
