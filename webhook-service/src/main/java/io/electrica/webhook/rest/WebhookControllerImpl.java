package io.electrica.webhook.rest;

import com.fasterxml.jackson.databind.JsonNode;
import io.electrica.webhook.dto.MessageResultDto;
import io.electrica.webhook.service.WebhookMessageResultDispatcher;
import io.electrica.webhook.service.WebhookMessageResultSender;
import io.electrica.webhook.service.WebhookMessageSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.Valid;
import java.util.UUID;

@RestController
public class WebhookControllerImpl implements WebhookController {

    private final WebhookMessageSender webhookMessageSender;
    private final WebhookMessageResultSender webhookMessageResultSender;
    private final WebhookMessageResultDispatcher webhookMessageResultDispatcher;
    private final long messageResultMaxTimeout;

    public WebhookControllerImpl(
            WebhookMessageSender webhookMessageSender,
            WebhookMessageResultSender webhookMessageResultSender,
            WebhookMessageResultDispatcher webhookMessageResultDispatcher,
            @Value("${webhook.message-result.max-timeout}") long messageResultMaxTimeout
    ) {
        this.webhookMessageSender = webhookMessageSender;
        this.webhookMessageResultSender = webhookMessageResultSender;
        this.webhookMessageResultDispatcher = webhookMessageResultDispatcher;
        this.messageResultMaxTimeout = messageResultMaxTimeout;
    }

    @Override
    @PreAuthorize("" +
            "#oauth2.hasScope('sdk') and " +
            "#webhook.webhookBelongsCurrentAccessKey(#webhookId) and " +
            "#webhook.validateAccessKey()"
    )
    public ResponseEntity<Void> submit(
            @PathVariable("webhookId") UUID webhookId,
            @RequestBody JsonNode payload
    ) {
        webhookMessageSender.send(webhookId, payload, false, false, null);
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<Void> publicSubmit(
            @PathVariable("webhookId") UUID webhookId,
            @PathVariable("sign") String sign,
            @RequestBody JsonNode payload
    ) {
        webhookMessageSender.send(webhookId, payload, false, true, sign);
        return ResponseEntity.accepted().build();
    }

    @Override
    @PreAuthorize("" +
            "#oauth2.hasScope('sdk') and " +
            "#webhook.webhookBelongsCurrentAccessKey(#webhookId) and " +
            "#webhook.validateAccessKey()"
    )
    public DeferredResult<JsonNode> invoke(
            @PathVariable("webhookId") UUID webhookId,
            @RequestBody JsonNode payload,
            @RequestParam(name = "timeout", required = false, defaultValue = "60000") long timeout
    ) {
        if (timeout > messageResultMaxTimeout) {
            timeout = messageResultMaxTimeout;
        }
        return webhookMessageResultDispatcher.submit(webhookId, payload, timeout, false, null);
    }

    @Override
    public DeferredResult<JsonNode> publicInvoke(
            @PathVariable("webhookId") UUID webhookId,
            @PathVariable("sign") String sign,
            @RequestBody JsonNode payload,
            @RequestParam(name = "timeout", required = false, defaultValue = "60000") long timeout
    ) {
        if (timeout > messageResultMaxTimeout) {
            timeout = messageResultMaxTimeout;
        }
        return webhookMessageResultDispatcher.submit(webhookId, payload, timeout, true, sign);
    }

    @Override
    @PreAuthorize("" +
            "#oauth2.hasScope('sdk') and " +
            "#webhook.validateAccessKey()"
    )
    public ResponseEntity<Void> submitMessageResult(@Valid @RequestBody MessageResultDto messageResult) {
        webhookMessageResultSender.send(messageResult);
        return ResponseEntity.accepted().build();
    }
}
