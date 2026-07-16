package ru.zipprey.eventify.auth.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zipprey.eventify.auth.exception.authentication.UserAlreadyExistException;
import ru.zipprey.eventify.auth.mappers.UserMapper;
import ru.zipprey.eventify.auth.model.dto.UserDto;
import ru.zipprey.eventify.auth.model.dto.request.RegisterRequest;
import ru.zipprey.eventify.auth.model.entity.User;
import ru.zipprey.eventify.auth.repository.RoleRepository;
import ru.zipprey.eventify.auth.repository.UserRepository;
import ru.zipprey.eventify.kafka.auth.UserRegisteredMessage;
import ru.zipprey.eventify.outbox.OutboxStatus;
import ru.zipprey.eventify.outbox.service.OutboxDataService;
import ru.zipprey.eventify.outbox.service.OutboxEventFactory;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final OutboxEventFactory eventFactory;
    private final OutboxDataService outboxDataService;

    @Override
    @Transactional
    public UserDto register(RegisterRequest request) {
        String requestEmail = request.getEmail();
        if (userRepository.findByEmail(requestEmail).isPresent()) {
            throw new UserAlreadyExistException(requestEmail);
        }

        var role = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Role USER not exists in repository"));

        var user = User.builder()
                .email(requestEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        var saved = userRepository.save(user);
        var event = eventFactory.create(
                "user.registered",
                saved.getEmail(),
                new UserRegisteredMessage(saved.getEmail()),
                OutboxStatus.READY
        );
        outboxDataService.add(event);
        return mapper.toDto(saved);
    }
}
