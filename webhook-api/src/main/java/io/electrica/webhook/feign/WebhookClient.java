package io.electrica.webhook.feign;

import com.fasterxml.jackson.databind.JsonNode;
import io.electrica.webhook.rest.WebhookController;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "webhookClient", url = "${feign.webhookService.url}")
public interface WebhookClient extends WebhookController {

    @PostMapping(PREFIX + "/{webhookId}" + SUBMIT_SUFFIX)
    ResponseEntity<Void> feignSubmit(
            @PathVariable("webhookId") UUID webhookId,
            @RequestBody JsonNode payload
    );

    @PostMapping(PUBLIC_PREFIX + "/{webhookId}/{sign}" + SUBMIT_SUFFIX)
    ResponseEntity<Void> feignPublicSubmit(
            @PathVariable("webhookId") UUID webhookId,
            @PathVariable("sign") String sign,
            @RequestBody JsonNode payload
    );

    @PostMapping(PREFIX + "/{webhookId}" + INVOKE_SUFFIX)
    ResponseEntity<JsonNode> feignInvoke(
            @PathVariable("webhookId") UUID webhookId,
            @RequestBody JsonNode payload,
            @RequestParam(name = "timeout", required = false, defaultValue = "60000") long timeout
    );

    @PostMapping(PUBLIC_PREFIX + "/{webhookId}/{sign}" + INVOKE_SUFFIX)
    ResponseEntity<JsonNode> feignPublicInvoke(
            @PathVariable("webhookId") UUID webhookId,
            @PathVariable("sign") String sign,
            @RequestBody JsonNode payload,
            @RequestParam(name = "timeout", required = false, defaultValue = "60000") long timeout
    );
}
