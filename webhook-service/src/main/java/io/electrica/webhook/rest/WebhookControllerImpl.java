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
public class WebhookControllerImpl implements WebhookController {

    private final WebhookDtoService webhookDtoService;

    public WebhookControllerImpl(WebhookDtoService webhookDtoService) {
        this.webhookDtoService = webhookDtoService;
    }

    @Override
    @PreAuthorize("" +
            "#webhook.hasPermission('CreateWebhook') and " +
            "#webhook.connectionBelongsCurrentUser(#dto.getConnectionId())"
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
}
