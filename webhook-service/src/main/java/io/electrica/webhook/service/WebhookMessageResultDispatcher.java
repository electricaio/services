package io.electrica.webhook.service;

import io.electrica.common.exception.TimeoutServiceException;
import io.electrica.webhook.dto.MessageResultDto;
import io.electrica.webhook.rest.TypedDeferredResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
public class WebhookMessageResultDispatcher {

    private final WebhookMessageSender webhookMessageSender;

    private final ConcurrentMap<UUID, TypedDeferredResult<String>> results = new ConcurrentHashMap<>();

    public WebhookMessageResultDispatcher(WebhookMessageSender webhookMessageSender) {
        this.webhookMessageSender = webhookMessageSender;
    }

    private TypedDeferredResult<String> createWebhookResult(
            UUID messageId,
            String expectedContentType,
            long timeout
    ) {
        TypedDeferredResult<String> result = new TypedDeferredResult<>(expectedContentType, timeout);
        result.onCompletion(() -> results.remove(messageId));
        result.onError(result::setErrorResult);
        result.onTimeout(() -> result.setErrorResult(new TimeoutServiceException()));
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
        UUID messageId = webhookMessageSender.send(webhookId, payload, contentType, expectedContentType,
                true, isPublic, sign);
        TypedDeferredResult<String> result = createWebhookResult(messageId, expectedContentType, timeout);
        results.put(messageId, result);
        return result;
    }

    public void handle(MessageResultDto messageResult) {
        UUID messageId = messageResult.getMessageId();
        TypedDeferredResult<String> result = results.get(messageId);
        if (result != null) {
            result.buildResponseEntityResult(messageResult.getPayload());
        }
    }
}
