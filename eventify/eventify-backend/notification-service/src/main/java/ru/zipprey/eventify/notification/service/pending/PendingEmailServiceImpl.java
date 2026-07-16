package ru.zipprey.eventify.notification.service.pending;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zipprey.eventify.notification.model.email.EmailTemplate;
import ru.zipprey.eventify.notification.model.email.PendingEmailStatus;
import ru.zipprey.eventify.notification.model.enity.PendingEmail;
import ru.zipprey.eventify.notification.repository.email.PendingEmailRepository;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PendingEmailServiceImpl implements PendingEmailService {

    private final PendingEmailRepository repository;
    private final ObjectMapper objectMapper;


    @Override
    @Transactional
    public void enqueue(String recipient, EmailTemplate template, Map<String, String> args) {
        var entity = PendingEmail.builder()
                .recipient(recipient)
                .template(template)
                .argumentsJson(objectMapper.writeValueAsString(args))
                .createdAt(Instant.now())
                .status(PendingEmailStatus.PENDING)
                .build();
        repository.save(entity);
    }

    @Override
    @Transactional
    public boolean markSent(Long id) {
        return repository.updateStatusIfCurrent(id, PendingEmailStatus.SENT, PendingEmailStatus.PENDING) > 0;
    }

    @Override
    @Transactional
    public boolean markSkippedUnconfirmed(Long id) {
        return repository.updateStatusIfCurrent(id, PendingEmailStatus.SKIPPED_UNCONFIRMED, PendingEmailStatus.PENDING) > 0;
    }
}
