package io.electrica.webhook.service;

import io.electrica.webhook.model.Webhook;
import io.electrica.webhook.repository.WebhookRepository;
import org.springframework.stereotype.Component;

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
        newEntity.setInvocationsCount(0L);
        return webhookRepository.save(newEntity);
    }
}
