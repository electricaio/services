package io.electrica.webhook.service;

import io.electrica.common.exception.BadRequestServiceException;
import io.electrica.webhook.model.Webhook;
import io.electrica.webhook.repository.WebhookRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class WebhookService {

    private final WebhookRepository webhookRepository;

    public WebhookService(WebhookRepository webhookRepository) {
        this.webhookRepository = webhookRepository;
    }

    public Webhook create(Webhook newEntity) {
        newEntity.setId(UUID.randomUUID());
        return webhookRepository.save(newEntity);
    }

    public List<Webhook> findByConnectionId(Long connectionId) {
        return webhookRepository.findAllByConnection(connectionId);
    }

    public Webhook findById(UUID id) {
        return webhookRepository.findById(id)
                .orElseThrow(() -> new BadRequestServiceException("Webhook not found: " + id));
    }

    public boolean webhookBelongsToUser(UUID webhookId, Long userId) {
        return webhookRepository.findById(webhookId)
                .filter(w -> userId.equals(w.getUserId()))
                .isPresent();
    }

    public void delete(UUID id) {
        Webhook entity = webhookRepository.getOne(id);
        webhookRepository.delete(entity);
    }
}
