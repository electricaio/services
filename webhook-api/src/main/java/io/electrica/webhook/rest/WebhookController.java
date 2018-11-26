package io.electrica.webhook.rest;

import io.electrica.webhook.dto.CreateWebhookDto;
import io.electrica.webhook.dto.WebhookDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static io.electrica.common.rest.PathConstants.V1;

public interface WebhookController {

    @PostMapping(V1 + "/webhooks")
    ResponseEntity<WebhookDto> create(@RequestBody CreateWebhookDto dto);

}
