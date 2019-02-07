package io.electrica.metric.connection.invocation.repository;

import io.electrica.metric.connection.invocation.model.ConnectionInvocation;
import io.electrica.metric.connection.invocation.model.ConnectionInvocationStatus;
import io.electrica.metric.instance.session.model.SessionState;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.UUID;

@AllArgsConstructor
public class ConnectionInvocationSpecification implements Specification<ConnectionInvocation> {
    private Long userId;
    private Long organizationId;
    private Long accessKeyId;
    private UUID instanceId;
    private Long connectionId;
    private Long connectorId;
    private EnumSet<ConnectionInvocationStatus> status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Override
    public Predicate toPredicate(Root<ConnectionInvocation> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate predicate = builder.and();

        predicate = equalIfNotNull(root, builder, predicate, "userId", userId);
        predicate = equalIfNotNull(root, builder, predicate, "organizationId", organizationId);
        predicate = equalIfNotNull(root, builder, predicate, "accessKeyId", accessKeyId);
        predicate = equalIfNotNull(root, builder, predicate, "instanceId", instanceId);
        predicate = equalIfNotNull(root, builder, predicate, "connectionId", connectionId);
        predicate = equalIfNotNull(root, builder, predicate, "connectorId", connectorId);

        if (status != null && !status.isEmpty()) {
            predicate = builder.and(predicate, root.<SessionState>get("status").in(status));
        }
        if (startTime != null && endTime != null) {
            Expression<LocalDateTime> endTimeExp = builder.coalesce(root.get("endTime"), LocalDateTime.now())
                    .as(LocalDateTime.class);
            predicate = builder.and(predicate, builder.or(
                    builder.between(root.get("startTime"), startTime, endTime),
                    builder.between(endTimeExp, startTime, endTime),
                    builder.and(
                            builder.lessThanOrEqualTo(root.get("startTime"), startTime),
                            builder.greaterThanOrEqualTo(endTimeExp, endTime)
                    ))
            );
        }

        return predicate;
    }

    private Predicate equalIfNotNull(Root<ConnectionInvocation> root, CriteriaBuilder builder, Predicate predicate,
                                     String fieldName, Object value) {
        if (value != null) {
            return builder.and(predicate, builder.equal(root.get(fieldName), value));
        }
        return predicate;
    }
}
