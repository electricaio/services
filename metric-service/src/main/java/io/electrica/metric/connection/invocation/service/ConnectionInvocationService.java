package io.electrica.metric.connection.invocation.service;

import io.electrica.metric.connection.invocation.model.ConnectionInvocation;
import io.electrica.metric.connection.invocation.model.ConnectionInvocationStatus;
import io.electrica.metric.connection.invocation.repository.ConnectionInvocationRepository;
import io.electrica.metric.connection.invocation.repository.ConnectionInvocationSpecification;
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
public class ConnectionInvocationService {
    private final ConnectionInvocationRepository repository;

    @Inject
    public ConnectionInvocationService(ConnectionInvocationRepository repository) {
        this.repository = repository;
    }

    @Retryable(
            value = {ObjectOptimisticLockingFailureException.class, DataIntegrityViolationException.class},
            backoff = @Backoff(delay = 0),
            maxAttempts = 5
    )
    public ConnectionInvocation upsert(ConnectionInvocation connectionInvocation) {
        ConnectionInvocation merged = repository.findById(connectionInvocation.getInvocationId())
                .map(e -> merge(e, connectionInvocation))
                .orElse(connectionInvocation);
        return repository.save(merged);
    }

    private ConnectionInvocation merge(ConnectionInvocation oldEntity, ConnectionInvocation newEntity) {
        if (oldEntity.getAction() == null) {
            oldEntity.setAction(newEntity.getAction());
        }
        if (oldEntity.getParameters() == null) {
            oldEntity.setParameters(newEntity.getParameters());
        }
        if (oldEntity.getPayload() == null) {
            oldEntity.setPayload(newEntity.getPayload());
        }
        if (oldEntity.getResult() == null) {
            oldEntity.setResult(newEntity.getResult());
        }
        if (oldEntity.getErrorCode() == null) {
            oldEntity.setErrorCode(newEntity.getErrorCode());
        }
        if (oldEntity.getErrorMessage() == null) {
            oldEntity.setErrorMessage(newEntity.getErrorMessage());
        }
        if (oldEntity.getStackTrace() == null) {
            oldEntity.setStackTrace(newEntity.getStackTrace());
        }
        if (oldEntity.getErrorPayload() == null) {
            oldEntity.setErrorPayload(newEntity.getErrorPayload());
        }
        if (oldEntity.getStatus() == ConnectionInvocationStatus.Pending) {
            oldEntity.setStatus(newEntity.getStatus());
        }
        return oldEntity;
    }

    public List<ConnectionInvocation> getConnectionInvocations(
            Pageable pageable,
            Long userId,
            Long organizationId,
            Long accessKeyId,
            UUID instanceId,
            Long connectionId,
            Long connectorId,
            Set<ConnectionInvocationStatus> status,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        EnumSet<ConnectionInvocationStatus> statuses = EnumSet.noneOf(ConnectionInvocationStatus.class);
        if (status != null) {
            statuses.addAll(status);
        }
        return repository.findAll(
                new ConnectionInvocationSpecification(
                        userId,
                        organizationId,
                        accessKeyId,
                        instanceId,
                        connectionId,
                        connectorId,
                        statuses,
                        startTime,
                        endTime
                ),
                pageable
        ).getContent();
    }
}
