package io.electrica.webhook.rest;

import io.electrica.webhook.dto.ConnectionCreateWebhookDto;
import io.electrica.webhook.dto.ConnectionWebhookDto;
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
public class WebhookManagementControllerImpl implements WebhookManagementController {

    private final WebhookDtoService webhookDtoService;

    public WebhookManagementControllerImpl(WebhookDtoService webhookDtoService) {
        this.webhookDtoService = webhookDtoService;
    }

    @Override
    @PreAuthorize("#webhook.hasPermission('ReadWebhook') AND #webhook.webhookBelongsCurrentUser(#webhookId)")
    public ResponseEntity<ConnectionWebhookDto> findById(@PathVariable("webhookId") UUID webhookId) {
        return ResponseEntity.ok(webhookDtoService.findById(webhookId));
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
    public ResponseEntity<Boolean> webhookBelongsCurrentUser(@PathVariable("webhookId") UUID webhookId) {
        return ResponseEntity.ok(webhookDtoService.webhookBelongsCurrentUser(webhookId));
    }
}
