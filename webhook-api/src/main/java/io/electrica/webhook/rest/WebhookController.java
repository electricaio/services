package io.electrica.webhook.rest;

import io.electrica.webhook.dto.CreateWebhookDto;
import io.electrica.webhook.dto.WebhookDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static io.electrica.common.rest.PathConstants.V1;

public interface WebhookController {

    @PostMapping(V1 + "/webhooks")
    ResponseEntity<WebhookDto> create(@RequestBody CreateWebhookDto dto);

    @GetMapping(V1 + "/connections/{connectionId}/webhooks")
    ResponseEntity<List<WebhookDto>> getByConnection(@PathVariable("connectionId") Long connectionId);

    @GetMapping(V1 + "/webhooks/{id}")
    ResponseEntity<WebhookDto> getById(@PathVariable("id") UUID id);

    @DeleteMapping(V1 + "/webhooks/{id}")
    ResponseEntity delete(@PathVariable("id") UUID id);

}
