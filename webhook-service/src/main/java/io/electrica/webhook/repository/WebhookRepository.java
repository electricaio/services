package io.electrica.webhook.repository;

import io.electrica.webhook.model.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookRepository extends JpaRepository<Webhook, Long> {


}
