package io.electrica.metric.common.mq.connection.invocation;

import com.fasterxml.jackson.databind.JsonNode;
import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityContextHolder;
import io.electrica.metric.common.mq.MetricSender;
import io.electrica.metric.common.mq.connection.invocation.event.ConnectionInvocationEvent;
import io.electrica.metric.common.mq.connection.invocation.event.ConnectionInvocationResultEvent;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static io.electrica.metric.common.mq.connection.invocation.ConnectionInvocationMetrics.CONNECTION_INVOCATION_RESULT_ROUTING_KEY;
import static io.electrica.metric.common.mq.connection.invocation.ConnectionInvocationMetrics.CONNECTION_INVOCATION_ROUTING_KEY;

@Component
public class ConnectionInvocationEventSender {
    private final MetricSender metricSender;
    private final IdentityContextHolder identityContextHolder;

    @Inject
    public ConnectionInvocationEventSender(MetricSender metricSender, IdentityContextHolder identityContextHolder) {
        this.metricSender = metricSender;
        this.identityContextHolder = identityContextHolder;
    }

    public void sendConnectionInvocation(UUID invocationId,
                                         UUID instanceId,
                                         LocalDateTime startTime,
                                         Long connectionId,
                                         Long connectionRevisionVersion,
                                         Long authorizationId,
                                         Long connectorId,
                                         Long connectorRevisionVersion,
                                         String ern,
                                         String action,
                                         JsonNode parameters,
                                         JsonNode payload) {
        Identity identity = identityContextHolder.getIdentity();
        metricSender.send(CONNECTION_INVOCATION_ROUTING_KEY, new ConnectionInvocationEvent(
                invocationId,
                instanceId,
                identity.getUserId(),
                identity.getOrganizationId(),
                identity.getAccessKeyId(),
                startTime,
                connectionId,
                connectionRevisionVersion,
                authorizationId,
                connectorId,
                connectorRevisionVersion,
                ern,
                action,
                parameters,
                payload
        ));
    }

    public void sendConnectionInvocationResult(UUID invocationId,
                                               UUID instanceId,
                                               LocalDateTime startTime,
                                               LocalDateTime endTime,
                                               Long connectionId,
                                               Long connectionRevisionVersion,
                                               Long authorizationId,
                                               Long connectorId,
                                               Long connectorRevisionVersion,
                                               String ern,
                                               Boolean success,
                                               JsonNode result,
                                               String errorCode,
                                               String errorMessage,
                                               String stackTrace,
                                               List<String> errorPayload) {
        Identity identity = identityContextHolder.getIdentity();
        metricSender.send(CONNECTION_INVOCATION_RESULT_ROUTING_KEY, new ConnectionInvocationResultEvent(
                invocationId,
                instanceId,
                identity.getUserId(),
                identity.getOrganizationId(),
                identity.getAccessKeyId(),
                startTime,
                endTime,
                connectionId,
                connectionRevisionVersion,
                authorizationId,
                connectorId,
                connectorRevisionVersion,
                ern,
                success,
                result,
                errorCode,
                errorMessage,
                stackTrace,
                errorPayload
        ));
    }
}
