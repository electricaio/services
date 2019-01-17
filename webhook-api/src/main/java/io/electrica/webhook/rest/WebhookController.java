package io.electrica.webhook.rest;

import com.fasterxml.jackson.databind.JsonNode;
import io.electrica.webhook.dto.MessageResultDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;

import static io.electrica.common.rest.PathConstants.PUBLIC;
import static io.electrica.common.rest.PathConstants.V1;

public interface WebhookController {

    String PREFIX = V1 + "/webhooks";
    String PUBLIC_PREFIX = PUBLIC + PREFIX;

    String SUBMIT_SUFFIX = "/submit";
    String INVOKE_SUFFIX = "/invoke";

    @PostMapping(PREFIX + "/{webhookId}" + SUBMIT_SUFFIX)
    ResponseEntity<Void> submit(
            @PathVariable("webhookId") UUID webhookId,
            @RequestBody JsonNode payload
    );

    @PostMapping(PUBLIC_PREFIX + "/{webhookId}/{sign}" + SUBMIT_SUFFIX)
    ResponseEntity<Void> publicSubmit(
            @PathVariable("webhookId") UUID webhookId,
            @PathVariable("sign") String sign,
            @RequestBody JsonNode payload
    );

    @PostMapping(PREFIX + "/{webhookId}" + INVOKE_SUFFIX)
    DeferredResult<JsonNode> invoke(
            @PathVariable("webhookId") UUID webhookId,
            @RequestBody JsonNode payload,
            @RequestParam(name = "timeout", required = false, defaultValue = "60000") long timeout
    );

    @PostMapping(PUBLIC_PREFIX + "/{webhookId}/{sign}" + INVOKE_SUFFIX)
    DeferredResult<JsonNode> publicInvoke(
            @PathVariable("webhookId") UUID webhookId,
            @PathVariable("sign") String sign,
            @RequestBody JsonNode payload,
            @RequestParam(name = "timeout", required = false, defaultValue = "60000") long timeout
    );

    @PostMapping(PREFIX + "/messages/result")
    ResponseEntity<Void> submitMessageResult(@RequestBody MessageResultDto messageResult);
}
