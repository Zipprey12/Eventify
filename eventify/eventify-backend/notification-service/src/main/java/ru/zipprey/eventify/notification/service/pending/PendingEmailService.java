package ru.zipprey.eventify.notification.service.pending;

import ru.zipprey.eventify.notification.model.email.EmailTemplate;

import java.util.Map;

public interface PendingEmailService {

    void enqueue(String recipient, EmailTemplate template, Map<String, String> args);

    boolean markSent(Long id);

    boolean markSkippedUnconfirmed(Long id);

}
