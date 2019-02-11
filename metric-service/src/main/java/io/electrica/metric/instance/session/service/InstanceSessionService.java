package io.electrica.metric.instance.session.service;

import io.electrica.metric.instance.session.dto.InstanceSessionFilter;
import io.electrica.metric.instance.session.model.InstanceSession;
import io.electrica.metric.instance.session.model.SessionState;
import io.electrica.metric.instance.session.repository.InstanceSessionRepository;
import io.electrica.metric.instance.session.repository.InstanceSessionSpecification;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Component
public class InstanceSessionService {
    private final InstanceSessionRepository repository;

    @Inject
    public InstanceSessionService(InstanceSessionRepository repository) {
        this.repository = repository;
    }

    public Optional<InstanceSession> findById(UUID id) {
        return repository.findById(id);
    }

    public Page<InstanceSession> findByFilter(InstanceSessionFilter filter, Pageable pageable) {
        return repository.findAll(new InstanceSessionSpecification(filter), pageable);
    }

    @Retryable(
            value = {OptimisticLockException.class, ConstraintViolationException.class},
            backoff = @Backoff(delay = 0),
            maxAttempts = 5
    )
    public InstanceSession changeState(InstanceSession newEntity) {
        InstanceSession merged = repository.findById(newEntity.getId())
                .map(oldEntity -> merge(oldEntity, newEntity))
                .orElseGet(() -> initStartedTime(newEntity));
        return repository.save(merged);
    }

    private InstanceSession merge(InstanceSession oldEntity, InstanceSession newEntity) {
        if (isRelevant(oldEntity, newEntity)) {
            oldEntity.setLastSessionStarted(newEntity.getLastSessionStarted());
            if (oldEntity.getSessionState().isTransitionAllowed(newEntity.getSessionState())) {
                oldEntity.setSessionState(newEntity.getSessionState());
                if (newEntity.getSessionState() == SessionState.Expired) {
                    oldEntity.setExpiredTime(LocalDateTime.now());
                    oldEntity.setStoppedTime(null);
                }
                if (newEntity.getSessionState() == SessionState.Running) {
                    oldEntity.setExpiredTime(null);
                    oldEntity.setStoppedTime(null);
                }
                if (newEntity.getSessionState() == SessionState.Stopped) {
                    oldEntity.setExpiredTime(null);
                    oldEntity.setStoppedTime(LocalDateTime.now());
                }
            }
        }

        oldEntity.setUserId(newEntity.getUserId());
        oldEntity.setOrganizationId(newEntity.getOrganizationId());
        oldEntity.setAccessKeyId(newEntity.getAccessKeyId());

        LocalDateTime newStartTime = getLastSessionStartedAsUTC(newEntity);
        if (oldEntity.getStartedTime().isAfter(newStartTime)) {
            oldEntity.setStartedTime(newStartTime);
        }

        return repository.save(oldEntity);
    }

    private boolean isRelevant(InstanceSession oldEntity, InstanceSession newEntity) {
        return oldEntity.getLastSessionStarted().isBefore(newEntity.getLastSessionStarted())
                || oldEntity.getLastSessionStarted().isEqual(newEntity.getLastSessionStarted());
    }

    private InstanceSession initStartedTime(InstanceSession instanceSession) {
        instanceSession.setStartedClientTime(instanceSession.getLastSessionStarted());
        LocalDateTime newStartTime = getLastSessionStartedAsUTC(instanceSession);
        instanceSession.setStartedTime(newStartTime);
        return instanceSession;
    }

    private LocalDateTime getLastSessionStartedAsUTC(InstanceSession instanceSession) {
        return LocalDateTime.ofInstant(
                instanceSession.getLastSessionStarted().toInstant(),
                ZoneOffset.UTC
        );
    }
}
