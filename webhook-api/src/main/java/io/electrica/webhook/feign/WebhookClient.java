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

import static io.electrica.common.rest.PathConstants.V1;

@FeignClient(name = "webhookClient", url = "${feign.webhookService.url}")
public interface WebhookClient extends WebhookController {

     @PostMapping(V1 + "/webhooks/{webhookId}/invoke")
     ResponseEntity<Object> invoke2(
            @PathVariable("webhookId") UUID webhookId,
            @RequestBody JsonNode payload,
            @RequestParam(name = "timeout", required = false, defaultValue = "60000") long timeout
    );
}
