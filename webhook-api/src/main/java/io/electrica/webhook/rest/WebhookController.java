package io.electrica.webhook.rest;

import io.electrica.webhook.dto.MessageResultDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;

import static io.electrica.common.rest.PathConstants.PUBLIC;
import static io.electrica.common.rest.PathConstants.V1;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

public interface WebhookController {

    String PREFIX = V1 + "/webhooks";
    String PUBLIC_PREFIX = PUBLIC + PREFIX;

    String SUBMIT_SUFFIX = "/submit";
    String INVOKE_SUFFIX = "/invoke";

    @PostMapping(PREFIX + "/{webhookId}" + SUBMIT_SUFFIX)
    ResponseEntity<Void> submit(
            @RequestHeader(value = CONTENT_TYPE, defaultValue = MediaType.TEXT_PLAIN_VALUE) String contentType,
            @PathVariable("webhookId") UUID webhookId,
            @RequestBody String payload
    );

    @PostMapping(PUBLIC_PREFIX + "/{webhookId}/{sign}" + SUBMIT_SUFFIX)
    ResponseEntity<Void> publicSubmit(
            @RequestHeader(value = CONTENT_TYPE, defaultValue = MediaType.TEXT_PLAIN_VALUE) String contentType,
            @PathVariable("webhookId") UUID webhookId,
            @PathVariable("sign") String sign,
            @RequestBody String payload
    );

    @PostMapping(PREFIX + "/{webhookId}" + INVOKE_SUFFIX)
    DeferredResult<ResponseEntity<String>> invoke(
            @RequestHeader(value = CONTENT_TYPE, defaultValue = MediaType.TEXT_PLAIN_VALUE) String contentType,
            @RequestHeader(value = ACCEPT, defaultValue = MediaType.ALL_VALUE) String accept,
            @PathVariable("webhookId") UUID webhookId,
            @RequestBody String payload,
            @RequestParam(name = "timeout", required = false, defaultValue = "60000") long timeout
    );

    @PostMapping(PUBLIC_PREFIX + "/{webhookId}/{sign}" + INVOKE_SUFFIX)
    DeferredResult<ResponseEntity<String>> publicInvoke(
            @RequestHeader(value = CONTENT_TYPE, defaultValue = MediaType.TEXT_PLAIN_VALUE) String contentType,
            @RequestHeader(value = ACCEPT, defaultValue = MediaType.ALL_VALUE) String accept,
            @PathVariable("webhookId") UUID webhookId,
            @PathVariable("sign") String sign,
            @RequestBody String payload,
            @RequestParam(name = "timeout", required = false, defaultValue = "60000") long timeout
    );

    @PostMapping(PREFIX + "/messages/result")
    ResponseEntity<Void> submitMessageResult(@RequestBody MessageResultDto messageResult);
}
