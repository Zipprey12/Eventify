package ru.zipprey.eventify.notification.service.email;

import ru.zipprey.eventify.notification.model.email.EmailMessageDto;

public interface EmailSender {

    void send(String to, EmailMessageDto messageDto);

}
