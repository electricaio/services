package io.electrica.metric.webhook.invocation.service;

import com.github.dozermapper.core.Mapper;
import io.electrica.metric.webhook.invocation.dto.WebhookInvocationDto;
import io.electrica.metric.webhook.invocation.model.WebhookInvocation;
import io.electrica.metric.webhook.invocation.model.WebhookInvocationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class WebhookInvocationDtoService {
    private final WebhookInvocationService webhookInvocationService;
    private final Mapper mapper;

    @Inject
    public WebhookInvocationDtoService(WebhookInvocationService webhookInvocationService, Mapper mapper) {
        this.webhookInvocationService = webhookInvocationService;
        this.mapper = mapper;
    }

    public WebhookInvocationDto upsert(WebhookInvocationDto webhookInvocationDto) {
        return toDto(webhookInvocationService.upsert(toEntity(webhookInvocationDto)));
    }

    public List<WebhookInvocationDto> getWebhookInvocations(Pageable pageable,
                                                            Long userId,
                                                            Long organizationId,
                                                            Long accessKeyId,
                                                            UUID webhookId,
                                                            Long webhookServiceId,
                                                            Set<WebhookInvocationStatus> status,
                                                            LocalDateTime startTime,
                                                            LocalDateTime endTime) {
        return webhookInvocationService.getWebhookInvocations(pageable, userId, organizationId, accessKeyId,
                webhookId, webhookServiceId, status, startTime, endTime).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private WebhookInvocationDto toDto(WebhookInvocation webhookInvocation) {
        return mapper.map(webhookInvocation, WebhookInvocationDto.class);
    }

    private WebhookInvocation toEntity(WebhookInvocationDto dto) {
        return mapper.map(dto, WebhookInvocation.class);
    }
}
