package io.electrica.webhook.service;

import io.electrica.common.exception.EntityNotFoundServiceException;
import io.electrica.webhook.model.Webhook;
import io.electrica.webhook.repository.WebhookRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class WebhookService {

    private final WebhookRepository webhookRepository;
    private final WebhookHashGenerator webhookHashGenerator;

    public WebhookService(WebhookRepository webhookRepository, WebhookHashGenerator webhookHashGenerator) {
        this.webhookRepository = webhookRepository;
        this.webhookHashGenerator = webhookHashGenerator;
    }

    public Webhook create(Webhook newEntity) {
        newEntity.setId(UUID.randomUUID());
        newEntity.setHash(webhookHashGenerator.generateHash(newEntity));
        return webhookRepository.save(newEntity);
    }

    public List<Webhook> findByConnectionId(Long connectionId) {
        return webhookRepository.findAllByConnection(connectionId);
    }

    public Webhook findById(UUID id) {
        return webhookRepository.getById(id)
                .orElseThrow(() -> new EntityNotFoundServiceException("Webhook entity not found: " + id));
    }
}
