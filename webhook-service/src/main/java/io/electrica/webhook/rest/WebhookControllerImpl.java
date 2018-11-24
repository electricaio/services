package io.electrica.webhook.rest;

import io.electrica.webhook.dto.CreateWebhookDto;
import io.electrica.webhook.dto.WebhookDto;
import io.electrica.webhook.service.dto.WebhookDtoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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

}
