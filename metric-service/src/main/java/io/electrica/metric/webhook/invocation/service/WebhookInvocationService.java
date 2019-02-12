package io.electrica.metric.webhook.invocation.service;

import io.electrica.metric.webhook.invocation.model.WebhookInvocation;
import io.electrica.metric.webhook.invocation.model.WebhookInvocationStatus;
import io.electrica.metric.webhook.invocation.repository.WebhookInvocationRepository;
import io.electrica.metric.webhook.invocation.repository.WebhookInvocationSpecification;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class WebhookInvocationService {
    private final WebhookInvocationRepository repository;

    @Inject
    public WebhookInvocationService(WebhookInvocationRepository repository) {
        this.repository = repository;
    }

    @Retryable(
            value = {ObjectOptimisticLockingFailureException.class, DataIntegrityViolationException.class},
            backoff = @Backoff(delay = 0),
            maxAttempts = 5
    )
    public WebhookInvocation upsert(WebhookInvocation webhookInvocation) {
        WebhookInvocation merged = repository.findById(webhookInvocation.getMessageId())
                .map(e -> merge(e, webhookInvocation))
                .orElse(webhookInvocation);
        if (merged.getEndTime() == null && Boolean.FALSE.equals(merged.getExpectedResult())) {
            merged.setEndTime(merged.getStartTime());
        }
        merged.setStatus(calculateStatus(merged));
        return repository.save(merged);
    }

    private WebhookInvocation merge(WebhookInvocation oldEntity, WebhookInvocation newEntity) {
        if (newEntity.getWebhookName() != null) {
            oldEntity.setWebhookName(newEntity.getWebhookName());
        }
        if (newEntity.getOrganizationId() != null) {
            oldEntity.setOrganizationId(newEntity.getOrganizationId());
        }
        if (newEntity.getUserId() != null) {
            oldEntity.setUserId(newEntity.getUserId());
        }
        if (newEntity.getAccessKeyId() != null) {
            oldEntity.setAccessKeyId(newEntity.getAccessKeyId());
        }
        if (newEntity.getIsPublic() != null) {
            oldEntity.setIsPublic(newEntity.getIsPublic());
        }
        if (newEntity.getScope() != null) {
            oldEntity.setScope(newEntity.getScope());
        }
        if (newEntity.getConnectorId() != null) {
            oldEntity.setConnectorId(newEntity.getConnectorId());
        }
        if (newEntity.getConnectorErn() != null) {
            oldEntity.setConnectorErn(newEntity.getConnectorErn());
        }
        if (newEntity.getConnectionId() != null) {
            oldEntity.setConnectionId(newEntity.getConnectionId());
        }
        if (newEntity.getProperties() != null) {
            oldEntity.setProperties(newEntity.getProperties());
        }
        if (newEntity.getExpectedResult() != null) {
            oldEntity.setExpectedResult(newEntity.getExpectedResult());
        }
        if (newEntity.getExpectedContentType() != null) {
            oldEntity.setExpectedContentType(newEntity.getExpectedContentType());
        }
        if (newEntity.getPayload() != null) {
            oldEntity.setPayload(newEntity.getPayload());
        }
        if (newEntity.getContentType() != null) {
            oldEntity.setContentType(newEntity.getContentType());
        }
        if (newEntity.getSdkInstanceId() != null) {
            oldEntity.setSdkInstanceId(newEntity.getSdkInstanceId());
        }
        if (newEntity.getResultPayload() != null) {
            oldEntity.setResultPayload(newEntity.getResultPayload());
        }
        if (newEntity.getStartTime() != null) {
            oldEntity.setStartTime(newEntity.getStartTime());
        }
        if (newEntity.getEndTime() != null) {
            oldEntity.setEndTime(newEntity.getEndTime());
        }
        if (newEntity.getErrorTime() != null) {
            oldEntity.setErrorTime(newEntity.getErrorTime());
        }
        if (newEntity.getErrorMessage() != null) {
            oldEntity.setErrorMessage(newEntity.getErrorMessage());
        }
        return oldEntity;
    }

    private WebhookInvocationStatus calculateStatus(WebhookInvocation webhookInvocation) {
        if (webhookInvocation.getErrorTime() != null) {
            return WebhookInvocationStatus.Error;
        }
        if (webhookInvocation.getEndTime() != null) {
            if (Boolean.FALSE.equals(webhookInvocation.getExpectedResult())) {
                return WebhookInvocationStatus.Invoked;
            } else {
                return WebhookInvocationStatus.Success;
            }
        }
        return WebhookInvocationStatus.Pending;
    }

    public List<WebhookInvocation> getWebhookInvocations(Pageable pageable,
                                                         Long userId,
                                                         Long organizationId,
                                                         Long accessKeyId,
                                                         UUID webhookId,
                                                         Long webhookServiceId,
                                                         Set<WebhookInvocationStatus> status,
                                                         LocalDateTime startTime,
                                                         LocalDateTime endTime) {
        EnumSet<WebhookInvocationStatus> statuses = EnumSet.noneOf(WebhookInvocationStatus.class);
        if (status != null) {
            statuses.addAll(status);
        }
        return repository.findAll(new WebhookInvocationSpecification(
                userId,
                organizationId,
                accessKeyId,
                webhookId,
                webhookServiceId,
                statuses,
                startTime,
                endTime
        ), pageable).getContent();
    }
}
