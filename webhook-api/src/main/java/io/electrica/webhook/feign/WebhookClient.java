package io.electrica.webhook.feign;

import io.electrica.webhook.rest.WebhookController;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "webhookClient", url = "${feign.webhookService.url}")
public interface WebhookClient extends WebhookController {
}
