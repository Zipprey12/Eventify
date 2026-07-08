package ru.zipprey.eventify.auth.exception.authentication;

public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException(String email){
        super("Пользователь с таким email уже создан: " + email);
    }
}
