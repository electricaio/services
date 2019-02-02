package io.electrica.metric.instance.session.service;

import io.electrica.common.exception.BadRequestServiceException;
import io.electrica.metric.instance.session.model.InstanceSession;
import io.electrica.metric.instance.session.model.SessionState;
import io.electrica.metric.instance.session.repository.InstanceSessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Component
public class InstanceSessionService {
    private final InstanceSessionRepository repository;

    @Inject
    public InstanceSessionService(InstanceSessionRepository repository) {
        this.repository = repository;
    }

    public Page<InstanceSession> findByOrganizationId(long organizationId, Pageable pageable) {
        return repository.findByOrganizationId(organizationId, pageable);
    }

    public Page<InstanceSession> findByUserId(long userId, Pageable pageable) {
        return repository.findByUserId(userId, pageable);
    }

    public Page<InstanceSession> findByAccessKeyId(long accessKeyId, Pageable pageable) {
        return repository.findByAccessKeyId(accessKeyId, pageable);
    }

    @Retryable(value = {OptimisticLockException.class, SQLException.class},
            backoff = @Backoff(delay = 0), maxAttempts = 5)
    public InstanceSession start(InstanceSession newEntity) {
        newEntity.setLastSessionStarted(newEntity.getStartedClientTime());
        return repository.findById(newEntity.getId())
                .map(e -> resume(e, newEntity.getLastSessionStarted()))
                .orElseGet(() -> repository.save(newEntity));
    }

    private InstanceSession resume(InstanceSession instanceSession, ZonedDateTime lastSessionStarted) {
        if (instanceSession.getLastSessionStarted().isBefore(lastSessionStarted)
                || instanceSession.getLastSessionStarted().isEqual(lastSessionStarted)) {
            instanceSession.setLastSessionStarted(lastSessionStarted);
            instanceSession.setExpiredTime(null);
            instanceSession.setSessionState(SessionState.Running);
            return repository.save(instanceSession);
        }
        return instanceSession;
    }

    @Retryable(value = OptimisticLockException.class, backoff = @Backoff(0), maxAttempts = 5)
    public InstanceSession expire(UUID id, ZonedDateTime lastSessionStarted) {
        checkSessionStartedTime(lastSessionStarted);
        InstanceSession instanceSession = findByIdOrElseThrow(id);
        if (instanceSession.getLastSessionStarted().isBefore(lastSessionStarted)) {
            checkTransition(instanceSession, SessionState.Expired);
            instanceSession.setExpiredTime(LocalDateTime.now());
            instanceSession.setSessionState(SessionState.Expired);
            return repository.save(instanceSession);
        }
        return instanceSession;
    }

    @Retryable(value = OptimisticLockException.class, backoff = @Backoff(0), maxAttempts = 5)
    public InstanceSession stop(UUID id, ZonedDateTime lastSessionStarted) {
        checkSessionStartedTime(lastSessionStarted);
        InstanceSession instanceSession = findByIdOrElseThrow(id);
        if (instanceSession.getLastSessionStarted().equals(lastSessionStarted)
                || instanceSession.getLastSessionStarted().isEqual(lastSessionStarted)) {
            checkTransition(instanceSession, SessionState.Stopped);
            instanceSession.setExpiredTime(null);
            instanceSession.setStoppedTime(LocalDateTime.now());
            instanceSession.setSessionState(SessionState.Stopped);
            return repository.save(instanceSession);
        }
        return instanceSession;
    }

    private InstanceSession findByIdOrElseThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new BadRequestServiceException("InstanceSession not found with " + id));
    }

    private void checkSessionStartedTime(ZonedDateTime lastSessionStarted) {
        if (lastSessionStarted == null) {
            throw new BadRequestServiceException("WebSocketSessionId is empty");
        }
    }

    private void checkTransition(InstanceSession instanceSession, SessionState toSessionState) {
        if (!instanceSession.getSessionState().isTransitionAllowed(toSessionState)) {
            throw new BadRequestServiceException(String.format(
                    "Transition isn't allowed from %s to %s for InstanceSession %s",
                    instanceSession.getSessionState(), toSessionState, instanceSession.getOrganizationId()
            ));
        }
    }
}
