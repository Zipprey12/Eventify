package ru.zipprey.eventify.auth.services;

import ru.zipprey.eventify.auth.model.dto.UserDto;
import ru.zipprey.eventify.auth.model.dto.request.RegisterRequest;

public interface UserService {

    UserDto register(RegisterRequest request);

}
