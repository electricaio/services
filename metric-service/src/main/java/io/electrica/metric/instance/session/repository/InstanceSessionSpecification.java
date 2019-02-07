package io.electrica.metric.instance.session.repository;

import io.electrica.metric.instance.session.dto.InstanceSessionFilter;
import io.electrica.metric.instance.session.model.InstanceSession;
import io.electrica.metric.instance.session.model.SessionState;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;

public class InstanceSessionSpecification implements Specification<InstanceSession> {
    private final InstanceSessionFilter instanceSessionFilter;

    public InstanceSessionSpecification(InstanceSessionFilter instanceSessionFilter) {
        this.instanceSessionFilter = instanceSessionFilter;
    }

    @Override
    public Predicate toPredicate(Root<InstanceSession> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate predicate = builder.and();

        if (instanceSessionFilter.getStartTime() != null && instanceSessionFilter.getEndTime() != null) {
            Expression<LocalDateTime> sessionendTime = builder.<LocalDateTime>coalesce()
                    .value(root.get("stoppedTime"))
                    .value(root.get("expiredTime"))
                    .value(LocalDateTime.now())
                    .as(LocalDateTime.class);
            predicate = builder.or(
                    builder.between(root.get("startedTime"),
                            instanceSessionFilter.getStartTime(), instanceSessionFilter.getEndTime()),
                    builder.between(sessionendTime,
                            instanceSessionFilter.getStartTime(), instanceSessionFilter.getEndTime()),
                    builder.and(
                            builder.lessThanOrEqualTo(root.get("startedTime"), instanceSessionFilter.getStartTime()),
                            builder.greaterThanOrEqualTo(sessionendTime, instanceSessionFilter.getEndTime())
                    )
            );
        }

        if (instanceSessionFilter.getNameStartWith() != null) {
            predicate = builder.and(predicate, builder.like(root.get("name"),
                    instanceSessionFilter.getNameStartWith() + "%"));
        }

        if (instanceSessionFilter.getAccessKeyId() != null) {
            predicate = builder.and(predicate, builder.equal(root.<Long>get("accessKeyId"),
                    instanceSessionFilter.getAccessKeyId()));
        }

        if (instanceSessionFilter.getUserId() != null) {
            predicate = builder.and(predicate, builder.equal(root.<Long>get("userId"),
                    instanceSessionFilter.getUserId()));
        }

        if (instanceSessionFilter.getOrganizationId() != null) {
            predicate = builder.and(predicate, builder.equal(root.<Long>get("organizationId"),
                    instanceSessionFilter.getOrganizationId()));
        }

        if (instanceSessionFilter.getSessionStates() != null && !instanceSessionFilter.getSessionStates().isEmpty()) {
            predicate = builder.and(predicate,
                    root.<SessionState>get("sessionState").in(instanceSessionFilter.getSessionStates()));
        }
        return predicate;
    }
}
