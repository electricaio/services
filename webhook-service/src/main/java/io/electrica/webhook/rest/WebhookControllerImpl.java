package io.electrica.webhook.rest;

import com.fasterxml.jackson.databind.JsonNode;
import io.electrica.webhook.dto.ConnectionCreateWebhookDto;
import io.electrica.webhook.dto.ConnectionWebhookDto;
import io.electrica.webhook.dto.MessageResultDto;
import io.electrica.webhook.service.WebhookMessageResultDispatcher;
import io.electrica.webhook.service.WebhookMessageResultSender;
import io.electrica.webhook.service.WebhookMessageSender;
import io.electrica.webhook.service.dto.WebhookDtoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
public class WebhookControllerImpl implements WebhookController {

    private final WebhookDtoService webhookDtoService;
    private final WebhookMessageSender webhookMessageSender;
    private final WebhookMessageResultSender webhookMessageResultSender;
    private final WebhookMessageResultDispatcher webhookMessageResultDispatcher;
    private final long messageResultMaxTimeout;

    public WebhookControllerImpl(
            WebhookDtoService webhookDtoService,
            WebhookMessageSender webhookMessageSender,
            WebhookMessageResultSender webhookMessageResultSender,
            WebhookMessageResultDispatcher webhookMessageResultDispatcher,
            @Value("${webhook.message-result.max-timeout}") long messageResultMaxTimeout
    ) {
        this.webhookDtoService = webhookDtoService;
        this.webhookMessageSender = webhookMessageSender;
        this.webhookMessageResultSender = webhookMessageResultSender;
        this.webhookMessageResultDispatcher = webhookMessageResultDispatcher;
        this.messageResultMaxTimeout = messageResultMaxTimeout;
    }

    @Override
    @PreAuthorize("" +
            "#webhook.hasPermission('CreateWebhook') and " +
            "#webhook.connectionBelongsCurrentUser(#dto.getConnectionId()) and " +
            "#webhook.connectionBelongsAccessKey(#dto.getConnectionId(), #dto.getAccessKeyId())"
    )
    public ResponseEntity<ConnectionWebhookDto> createConnection(@Valid @RequestBody ConnectionCreateWebhookDto dto) {
        ConnectionWebhookDto result = webhookDtoService.createConnection(dto);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#webhook.hasPermission('ReadWebhook')")
    @PostAuthorize("#webhook.connectionWebhooksBelongsCurrentUser(returnObject.getBody())")
    public ResponseEntity<List<ConnectionWebhookDto>> getByConnection(@PathVariable("connectionId") Long connectionId) {
        List<ConnectionWebhookDto> result = webhookDtoService.findAllByConnectionId(connectionId);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#webhook.hasPermission('DeleteWebhook') AND #webhook.webhookBelongsCurrentUser(#webhookId)")
    public void delete(@PathVariable("webhookId") UUID webhookId) {
        webhookDtoService.delete(webhookId);
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
        webhookMessageSender.send(webhookId, payload, false);
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
        return webhookMessageResultDispatcher.submit(webhookId, payload, timeout);
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
