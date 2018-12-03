package io.electrica.webhook.rest;

import com.fasterxml.jackson.databind.JsonNode;
import io.electrica.webhook.dto.ConnectionCreateWebhookDto;
import io.electrica.webhook.dto.ConnectionWebhookDto;
import io.electrica.webhook.service.WebhookMessageSender;
import io.electrica.webhook.service.dto.WebhookDtoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
public class WebhookControllerImpl implements WebhookController {

    private final WebhookDtoService webhookDtoService;
    private final WebhookMessageSender webhookMessageSender;

    public WebhookControllerImpl(WebhookDtoService webhookDtoService, WebhookMessageSender webhookMessageSender) {
        this.webhookDtoService = webhookDtoService;
        this.webhookMessageSender = webhookMessageSender;
    }

    @Override
    @PreAuthorize("" +
            "#webhook.hasPermission('CreateWebhook') and " +
            "#webhook.connectionBelongsCurrentUser(#dto.getConnectionId()) and " +
            "#webhook.connectionBelongsAccessKey(#dto.getConnectionId(), #dto.getAccessKeyId())"
    )
    public ResponseEntity<ConnectionWebhookDto> createConnection(@Valid @RequestBody ConnectionCreateWebhookDto dto) {
        return ResponseEntity.ok(webhookDtoService.createConnection(dto));
    }

    @Override
    @PreAuthorize("#webhook.hasPermission('ReadWebhook')")
    @PostAuthorize("#webhook.connectionWebhooksBelongsCurrentUser(returnObject.getBody())")
    public ResponseEntity<List<ConnectionWebhookDto>> getByConnection(@PathVariable("connectionId") Long connectionId) {
        List<ConnectionWebhookDto> result = webhookDtoService.findAllByConnectionId(connectionId);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#webhook.hasPermission('DeleteWebhook') AND #webhook.webhookBelongsCurrentUser(#id)")
    public void delete(@PathVariable("id") UUID id) {
        webhookDtoService.delete(id);
    }

    @Override
    @PreAuthorize("#oauth2.hasScope('sdk') and #webhook.webhookBelongsCurrentAccessKey(#webhookId)")
    public ResponseEntity sendMessage(
            @PathVariable("webhookId") UUID webhookId,
            @RequestBody JsonNode payload
    ) {
        webhookMessageSender.send(webhookId, payload);
        return ResponseEntity.accepted().build();
    }

    @Override
    @PreAuthorize("#oauth2.hasScope('sdk') and #webhook.webhookBelongsCurrentAccessKey(#webhookId)")
    public ResponseEntity<JsonNode> invoke(
            @PathVariable("webhookId") UUID webhookId,
            @RequestBody JsonNode payload
    ) {
        // TODO
        return ResponseEntity.ok(payload);
    }
}
