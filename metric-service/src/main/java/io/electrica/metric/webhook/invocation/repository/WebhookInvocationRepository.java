package io.electrica.metric.webhook.invocation.repository;

import io.electrica.metric.webhook.invocation.model.WebhookInvocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface WebhookInvocationRepository extends JpaRepository<WebhookInvocation, UUID>,
        JpaSpecificationExecutor<WebhookInvocation> {
}
