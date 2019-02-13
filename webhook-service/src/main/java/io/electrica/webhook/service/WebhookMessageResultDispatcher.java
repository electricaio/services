package io.electrica.webhook.service;

import io.electrica.common.context.Identity;
import io.electrica.common.exception.TimeoutServiceException;
import io.electrica.metric.common.mq.webhook.invocation.WebhookInvocationSender;
import io.electrica.webhook.dto.MessageResultDto;
import io.electrica.webhook.message.WebhookMessage;
import io.electrica.webhook.rest.TypedDeferredResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
public class WebhookMessageResultDispatcher {

    private final WebhookMessageSender webhookMessageSender;
    private final WebhookInvocationSender webhookInvocationSender;

    private final ConcurrentMap<UUID, TypedDeferredResult<String>> results = new ConcurrentHashMap<>();

    public WebhookMessageResultDispatcher(WebhookMessageSender webhookMessageSender,
                                          WebhookInvocationSender webhookInvocationSender) {
        this.webhookMessageSender = webhookMessageSender;
        this.webhookInvocationSender = webhookInvocationSender;
    }

    private TypedDeferredResult<String> createWebhookResult(
            UUID messageId,
            String expectedContentType,
            long timeout,
            Long organizationId,
            Long userId,
            Long accessKeyId
    ) {
        TypedDeferredResult<String> result = new TypedDeferredResult<>(expectedContentType, timeout,
                organizationId, userId, accessKeyId);
        result.onCompletion(() -> results.remove(messageId));
        result.onError(e -> {
            result.setErrorResult(e);
            webhookInvocationSender.sendError(messageId, LocalDateTime.now(), e.getMessage(),
                    organizationId, userId, accessKeyId);
        });
        result.onTimeout(() -> {
            TimeoutServiceException timeoutServiceException = new TimeoutServiceException();
            result.setErrorResult(timeoutServiceException);
            webhookInvocationSender.sendError(messageId, LocalDateTime.now(),
                    "Webhook timeout exception " + timeout + "ms",
                    organizationId, userId, accessKeyId);
        });
        return result;
    }

    public DeferredResult<ResponseEntity<String>> submit(
            UUID webhookId,
            String payload,
            String contentType,
            @Nullable String expectedContentType,
            long timeout,
            boolean isPublic,
            @Nullable String sign
    ) {
        WebhookMessage message = webhookMessageSender.send(webhookId, payload, contentType, expectedContentType,
                true, isPublic, sign);
        UUID messageId = message.getId();
        TypedDeferredResult<String> result = createWebhookResult(messageId, expectedContentType, timeout,
                message.getOrganizationId(), message.getUserId(), message.getAccessKeyId());
        results.put(messageId, result);
        return result;
    }

    public void handle(MessageResultDto messageResult) {
        UUID messageId = messageResult.getMessageId();
        TypedDeferredResult<String> result = results.get(messageId);
        Long organizationId = Identity.NOT_AUTHENTICATED_ORGANIZATION_ID;
        Long userId = Identity.NOT_AUTHENTICATED_USER_ID;
        Long accessKeyId = null;
        if (result != null) {
            result.buildResponseEntityResult(messageResult.getPayload());
            organizationId = result.getOrganizationId();
            userId = result.getUserId();
            accessKeyId = result.getAccessKeyId();
        }
        webhookInvocationSender.sendResult(messageId, LocalDateTime.now(), messageResult,
                organizationId, userId, accessKeyId);
    }
}
