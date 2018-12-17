package io.electrica.webhook.service;

import com.fasterxml.jackson.databind.JsonNode;
import io.electrica.common.exception.TimeoutServiceException;
import io.electrica.webhook.dto.MessageResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
public class WebhookMessageResultDispatcher {

    private final WebhookMessageSender webhookMessageSender;

    private final ConcurrentMap<UUID, DeferredResult<JsonNode>> results = new ConcurrentHashMap<>();

    public WebhookMessageResultDispatcher(WebhookMessageSender webhookMessageSender) {
        this.webhookMessageSender = webhookMessageSender;
    }

    private DeferredResult<JsonNode> createWebhookResult(UUID messageId, long timeout) {
        DeferredResult<JsonNode> result = new DeferredResult<>(timeout);
        result.onCompletion(() -> results.remove(messageId));
        result.onError(result::setErrorResult);
        result.onTimeout(() -> result.setErrorResult(new TimeoutServiceException()));
        return result;
    }

    public DeferredResult<JsonNode> submit(UUID webhookId, JsonNode payload, long timeout) {
        UUID messageId = webhookMessageSender.send(webhookId, payload, true);
        DeferredResult<JsonNode> result = createWebhookResult(messageId, timeout);
        results.put(messageId, result);
        return result;
    }

    public void handle(MessageResultDto messageResult) {
        UUID messageId = messageResult.getMessageId();
        DeferredResult<JsonNode> result = results.get(messageId);
        if (result != null) {
            result.setResult(messageResult.getPayload());
        }
    }
}
