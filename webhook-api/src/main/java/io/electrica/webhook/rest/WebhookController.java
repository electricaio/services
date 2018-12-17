package io.electrica.webhook.rest;

import com.fasterxml.jackson.databind.JsonNode;
import io.electrica.webhook.dto.ConnectionCreateWebhookDto;
import io.electrica.webhook.dto.ConnectionWebhookDto;
import io.electrica.webhook.dto.MessageResultDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.UUID;

import static io.electrica.common.rest.PathConstants.V1;

public interface WebhookController {

    @PostMapping(V1 + "/webhooks/connection")
    ResponseEntity<ConnectionWebhookDto> createConnection(@RequestBody ConnectionCreateWebhookDto dto);

    @GetMapping(V1 + "/connections/{connectionId}/webhooks")
    ResponseEntity<List<ConnectionWebhookDto>> getByConnection(@PathVariable("connectionId") Long connectionId);

    @DeleteMapping(V1 + "/webhooks/{webhookId}")
    void delete(@PathVariable("webhookId") UUID webhookId);

    @PostMapping(V1 + "/webhooks/{webhookId}/submit")
    ResponseEntity<Void> submit(
            @PathVariable("webhookId") UUID webhookId,
            @RequestBody JsonNode payload
    );

    @PostMapping(V1 + "/webhooks/{webhookId}/invoke")
    DeferredResult<JsonNode> invoke(
            @PathVariable("webhookId") UUID webhookId,
            @RequestBody JsonNode payload,
            @RequestParam(name = "timeout", required = false, defaultValue = "60000") long timeout
    );

    @PostMapping(V1 + "/webhooks/messages/result")
    ResponseEntity<Void> submitMessageResult(@RequestBody MessageResultDto messageResult);

}
