package io.electrica.metric.common.mq.webhook.invocation;

import io.electrica.metric.common.mq.MetricSender;
import io.electrica.metric.common.mq.webhook.invocation.event.WebhookInvocationEvent;
import io.electrica.metric.common.mq.webhook.invocation.event.WebhookInvocationResultEvent;
import io.electrica.webhook.dto.MessageResultDto;
import io.electrica.webhook.message.WebhookMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.UUID;

import static io.electrica.metric.common.mq.webhook.invocation.WebhookInvcoationMetrics.WEBHOOK_INVOCATION_RESULT_ROUTING_KEY;
import static io.electrica.metric.common.mq.webhook.invocation.WebhookInvcoationMetrics.WEBHOOK_INVOCATION_ROUTING_KEY;

@Component
public class WebhookInvocationSender {
    private final MetricSender metricSender;

    @Inject
    public WebhookInvocationSender(MetricSender metricSender) {
        this.metricSender = metricSender;
    }

    public void send(WebhookMessage webhookMessage, LocalDateTime startTime) {
        metricSender.send(WEBHOOK_INVOCATION_ROUTING_KEY, new WebhookInvocationEvent(
                startTime,
                webhookMessage
        ));
    }

    public void sendResult(UUID messageId, LocalDateTime endTime, MessageResultDto messageResultDto) {
        sendResult(messageId, endTime, messageResultDto, null, null);
    }

    public void sendErrorResult(UUID messageId, LocalDateTime endTime, String errorMessage, String stackTrace) {
        sendResult(messageId, endTime, null, errorMessage, stackTrace);
    }

    private void sendResult(UUID messageId, LocalDateTime endTime, @Nullable MessageResultDto messageResultDto,
                           @Nullable String errorMessage, @Nullable String stackTrace) {
        messageResultDto = messageResultDto == null ? new MessageResultDto() : messageResultDto;
        metricSender.send(WEBHOOK_INVOCATION_RESULT_ROUTING_KEY, new WebhookInvocationResultEvent(
                messageId,
                endTime,
                messageResultDto.getWebhookId(),
                messageResultDto.getSdkInstanceId(),
                messageResultDto.getWebhookServiceId(),
                messageResultDto.getPayload(),
                errorMessage,
                stackTrace
        ));
    }
}
