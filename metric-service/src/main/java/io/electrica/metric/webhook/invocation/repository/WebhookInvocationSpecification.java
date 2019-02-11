package io.electrica.metric.webhook.invocation.repository;

import io.electrica.metric.instance.session.model.SessionState;
import io.electrica.metric.webhook.invocation.model.WebhookInvocation;
import io.electrica.metric.webhook.invocation.model.WebhookInvocationStatus;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.UUID;

public class WebhookInvocationSpecification implements Specification<WebhookInvocation> {
    private final Long userId;
    private final Long organizationId;
    private final Long accessKeyId;
    private final UUID webhookId;
    private final Long webhookServiceId;
    private final EnumSet<WebhookInvocationStatus> status;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public WebhookInvocationSpecification(Long userId, Long organizationId, Long accessKeyId,
                                          UUID webhookId, Long webhookServiceId,
                                          EnumSet<WebhookInvocationStatus> status, LocalDateTime startTime,
                                          LocalDateTime endTime) {
        this.userId = userId;
        this.organizationId = organizationId;
        this.accessKeyId = accessKeyId;
        this.webhookId = webhookId;
        this.webhookServiceId = webhookServiceId;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public Predicate toPredicate(Root<WebhookInvocation> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate predicate = builder.and();

        predicate = equalIfNotNull(root, builder, predicate, "userId", userId);
        predicate = equalIfNotNull(root, builder, predicate, "organizationId", organizationId);
        predicate = equalIfNotNull(root, builder, predicate, "accessKeyId", accessKeyId);
        predicate = equalIfNotNull(root, builder, predicate, "webhookId", webhookId);
        predicate = equalIfNotNull(root, builder, predicate, "webhookServiceId", webhookServiceId);

        if (status != null && !status.isEmpty()) {
            predicate = builder.and(predicate, root.<SessionState>get("status").in(status));
        }
        if (startTime != null && endTime != null) {
            Expression<LocalDateTime> startTimeExp = builder.coalesce(root.get("startTime"), root.get("endTime"))
                    .as(LocalDateTime.class);
            Expression<LocalDateTime> endTimeExp = builder.coalesce(root.get("endTime"), LocalDateTime.now())
                    .as(LocalDateTime.class);
            predicate = builder.and(predicate, builder.or(
                    builder.between(startTimeExp, startTime, endTime),
                    builder.between(endTimeExp, startTime, endTime),
                    builder.and(
                            builder.lessThanOrEqualTo(startTimeExp, startTime),
                            builder.greaterThanOrEqualTo(endTimeExp, endTime)
                    ))
            );
        }

        return predicate;
    }


    private Predicate equalIfNotNull(Root<WebhookInvocation> root, CriteriaBuilder builder, Predicate predicate,
                                     String fieldName, Object value) {
        if (value != null) {
            return builder.and(predicate, builder.equal(root.get(fieldName), value));
        }
        return predicate;
    }
}
