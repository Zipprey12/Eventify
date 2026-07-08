package ru.zipprey.eventify.auth.services;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.zipprey.eventify.auth.mappers.UserMapper;
import ru.zipprey.eventify.auth.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var found = userRepository.findByEmail(email);
        if (found.isEmpty()) {
            throw UsernameNotFoundException.fromUsername(email);
        }
        return mapper.toPrincipal(found.get());
    }
}
