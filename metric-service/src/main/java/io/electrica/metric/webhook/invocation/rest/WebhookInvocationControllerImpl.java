package io.electrica.metric.webhook.invocation.rest;

import io.electrica.metric.webhook.invocation.dto.WebhookInvocationDto;
import io.electrica.metric.webhook.invocation.model.WebhookInvocationStatus;
import io.electrica.metric.webhook.invocation.service.WebhookInvocationDtoService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
public class WebhookInvocationControllerImpl implements WebhookInvocationController {
    private final WebhookInvocationDtoService webhookInvocationDtoService;

    @Inject
    public WebhookInvocationControllerImpl(WebhookInvocationDtoService webhookInvocationDtoService) {
        this.webhookInvocationDtoService = webhookInvocationDtoService;
    }

    @PreAuthorize("" +
            "#common.hasPermission('ReadWebhookInvocation') AND ( " +
            " #common.isUser(#userId) OR " +
            " #metric.accessKeyBelongsUser(#accessKeyId) OR " +
            " #metric.webhookBelongsCurrentUser(#webhookId) OR " +
            " #common.isSuperAdmin()" +
            ")")
    @Override
    public List<WebhookInvocationDto> getWebhookInvocations(
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
    ) {
        return webhookInvocationDtoService.getWebhookInvocations(pageable, userId, organizationId, accessKeyId,
                webhookId, webhookServiceId, status, startTime, endTime);
    }
}
