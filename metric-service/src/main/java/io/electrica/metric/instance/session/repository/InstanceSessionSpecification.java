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

        if (instanceSessionFilter.getStartDate() != null && instanceSessionFilter.getEndDate() != null) {
            Expression<LocalDateTime> sessionEndDate = builder.<LocalDateTime>coalesce()
                    .value(root.get("stoppedTime"))
                    .value(root.get("expiredTime"))
                    .value(LocalDateTime.now())
                    .as(LocalDateTime.class);
            predicate = builder.or(
                    builder.between(root.get("startedTime"),
                            instanceSessionFilter.getStartDate(), instanceSessionFilter.getEndDate()),
                    builder.between(sessionEndDate,
                            instanceSessionFilter.getStartDate(), instanceSessionFilter.getEndDate()),
                    builder.and(
                            builder.lessThanOrEqualTo(root.get("startedTime"), instanceSessionFilter.getStartDate()),
                            builder.greaterThanOrEqualTo(sessionEndDate, instanceSessionFilter.getEndDate())
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
