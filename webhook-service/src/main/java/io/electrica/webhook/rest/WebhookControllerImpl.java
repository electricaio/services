package io.electrica.webhook.rest;

import io.electrica.webhook.dto.CreateWebhookDto;
import io.electrica.webhook.dto.WebhookDto;
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

    @PreAuthorize("#common.hasPermission('CreateWebhook') AND  #webhook.canUserAccess(#dto.getConnectionId())")
    @Override
    public ResponseEntity<WebhookDto> create(@Valid @RequestBody CreateWebhookDto dto) {
        return ResponseEntity.ok(webhookDtoService.create(dto));
    }

    @PreAuthorize("#common.hasPermission('ReadWebhook')")
    @PostAuthorize("#webhook.canUserAccess(returnObject.getBody().getConnectionId())")
    @Override
    public ResponseEntity<WebhookDto> getById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(webhookDtoService.findById(id));
    }

    @Override
    @PreAuthorize("#common.hasPermission('ReadWebhook') AND #webhook.canUserAccess(#connectionId)")
    public ResponseEntity<List<WebhookDto>> getByConnection(@PathVariable("connectionId") Long connectionId) {
        List<WebhookDto> webhooks = webhookDtoService.findAllByConnectionId(connectionId);
        return ResponseEntity.ok(webhooks);
    }

}
