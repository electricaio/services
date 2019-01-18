package io.electrica.webhook.feign;

import io.electrica.webhook.rest.WebhookManagementController;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "webhookManagementClient", url = "${feign.webhookService.url}")
public interface WebhookManagementClient extends WebhookManagementController {
}
