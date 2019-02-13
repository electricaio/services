package io.electrica.metric.common.mq.webhook.invocation;

import io.electrica.metric.common.mq.MetricSender;
import io.electrica.metric.common.mq.webhook.invocation.event.WebhookInvocationErrorEvent;
import io.electrica.metric.common.mq.webhook.invocation.event.WebhookInvocationEvent;
import io.electrica.metric.common.mq.webhook.invocation.event.WebhookInvocationResultEvent;
import io.electrica.webhook.dto.MessageResultDto;
import io.electrica.webhook.message.WebhookMessage;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.UUID;

import static io.electrica.metric.common.mq.webhook.invocation.WebhookInvcoationMetrics.*;

@Component
public class WebhookInvocationSender {
    private final MetricSender metricSender;

    @Inject
    public WebhookInvocationSender(MetricSender metricSender) {
        this.metricSender = metricSender;
    }

    public void send(WebhookMessage webhookMessage, LocalDateTime startTime) {
        metricSender.send(WEBHOOK_INVOCATION_ROUTING_KEY, new WebhookInvocationEvent(
                webhookMessage.getId(),
                startTime,
                webhookMessage.getWebhookId(),
                webhookMessage.getWebhookServiceId(),
                webhookMessage.getName(),
                webhookMessage.getOrganizationId(),
                webhookMessage.getUserId(),
                webhookMessage.getAccessKeyId(),
                webhookMessage.getIsPublic(),
                webhookMessage.getScope(),
                webhookMessage.getConnectorId(),
                webhookMessage.getConnectorErn(),
                webhookMessage.getConnectionId(),
                webhookMessage.getProperties(),
                webhookMessage.getExpectedResult(),
                webhookMessage.getExpectedContentType(),
                webhookMessage.getPayload(),
                webhookMessage.getContentType()
        ));
    }

    public void sendResult(UUID messageId, LocalDateTime endTime, MessageResultDto messageResultDto,
                           Long organizationId, Long userId, Long accessKeyId) {
        metricSender.send(WEBHOOK_INVOCATION_RESULT_ROUTING_KEY, new WebhookInvocationResultEvent(
                messageId,
                endTime,
                messageResultDto.getWebhookId(),
                messageResultDto.getSdkInstanceId(),
                messageResultDto.getWebhookServiceId(),
                messageResultDto.getPayload(),
                organizationId,
                userId,
                accessKeyId
        ));
    }

    public void sendError(UUID messageId, LocalDateTime errorTime, String errorMessage,
                          Long organizationId, Long userId, Long accessKeyId) {
        metricSender.send(WEBHOOK_INVOCATION_ERROR_ROUTING_KEY, new WebhookInvocationErrorEvent(
                messageId,
                errorMessage,
                errorTime,
                organizationId,
                userId,
                accessKeyId
        ));
    }
}
