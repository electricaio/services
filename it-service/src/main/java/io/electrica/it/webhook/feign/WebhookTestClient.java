package io.electrica.it.webhook.feign;

import io.electrica.webhook.rest.WebhookController;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "webhookTestClient", url = "${feign.webhookService.url}")
public interface WebhookTestClient extends WebhookController {

    @PostMapping(path = PREFIX + "/{webhookId}" + INVOKE_SUFFIX,
            consumes = "application/json", produces = "application/json")
    ResponseEntity<String> invokeJson(
            @PathVariable("webhookId") UUID webhookId,
            @RequestBody String payload,
            @RequestParam(name = "timeout", required = false, defaultValue = "60000") long timeout
    );

    @PostMapping(path = PREFIX + "/{webhookId}" + INVOKE_SUFFIX,
            consumes = "application/xml", produces = "application/xml")
    ResponseEntity<String> invokeXml(
            @PathVariable("webhookId") UUID webhookId,
            @RequestBody String payload,
            @RequestParam(name = "timeout", required = false, defaultValue = "60000") long timeout
    );

    @PostMapping(path = PREFIX + "/{webhookId}" + INVOKE_SUFFIX,
            consumes = "text/plain", produces = "text/plain")
    ResponseEntity<String> invokeTextPlain(
            @PathVariable("webhookId") UUID webhookId,
            @RequestBody String payload,
            @RequestParam(name = "timeout", required = false, defaultValue = "60000") long timeout
    );

    @PostMapping(PREFIX + "/{webhookId}" + SUBMIT_SUFFIX)
    ResponseEntity<Void> submit(
            @PathVariable("webhookId") UUID webhookId,
            @RequestBody String payload
    );

    @PostMapping(PUBLIC_PREFIX + "/{webhookId}/{sign}" + SUBMIT_SUFFIX)
    ResponseEntity<Void> publicSubmit(
            @PathVariable("webhookId") UUID webhookId,
            @PathVariable("sign") String sign,
            @RequestBody String payload
    );

    @PostMapping(PREFIX + "/{webhookId}" + INVOKE_SUFFIX)
    ResponseEntity<String> invoke(
            @PathVariable("webhookId") UUID webhookId,
            @RequestBody String payload,
            @RequestParam(name = "timeout", required = false, defaultValue = "60000") long timeout
    );

    @PostMapping(PUBLIC_PREFIX + "/{webhookId}/{sign}" + INVOKE_SUFFIX)
    ResponseEntity<String> publicInvoke(
            @PathVariable("webhookId") UUID webhookId,
            @PathVariable("sign") String sign,
            @RequestBody String payload,
            @RequestParam(name = "timeout", required = false, defaultValue = "60000") long timeout
    );
}
