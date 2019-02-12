package io.electrica.metric.webhook.invocation.rest;

import io.electrica.metric.webhook.invocation.dto.WebhookInvocationDto;
import io.electrica.metric.webhook.invocation.model.WebhookInvocationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static io.electrica.common.rest.PathConstants.V1;

public interface WebhookInvocationController {
    String PREFIX = V1 + "/metrics/webhook-invocations";

    @GetMapping(path = PREFIX + "/webhook-invocations")
    List<WebhookInvocationDto> getWebhookInvocations(
            @PageableDefault Pageable pageable,
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
