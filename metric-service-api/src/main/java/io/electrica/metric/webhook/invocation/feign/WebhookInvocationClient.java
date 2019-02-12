package io.electrica.metric.webhook.invocation.feign;

import io.electrica.metric.webhook.invocation.dto.WebhookInvocationDto;
import io.electrica.metric.webhook.invocation.model.WebhookInvocationStatus;
import io.electrica.metric.webhook.invocation.rest.WebhookInvocationController;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@FeignClient(name = "webhookInvocationClient", url = "${feign.metricService.url}")
public interface WebhookInvocationClient extends WebhookInvocationController {

    @GetMapping(path = PREFIX + "/webhook-invocations")
    List<WebhookInvocationDto> feignGetWebhookInvocations(
            @RequestParam(value = "pageNumber", required = false) int pageNumber,
            @RequestParam(value = "pageSize", required = false) int pageSize,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "organizationId", required = false) Long organizationId,
            @RequestParam(value = "accessKeyId", required = false) Long accessKeyId,
            @RequestParam(value = "webhookId", required = false) UUID webhookId,
            @RequestParam(value = "webhookServiceId", required = false) Long webhookServiceId,
            @RequestParam(value = "status[]", required = false) Set<WebhookInvocationStatus> status,
            @RequestParam(value = "startTime", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    );
}
