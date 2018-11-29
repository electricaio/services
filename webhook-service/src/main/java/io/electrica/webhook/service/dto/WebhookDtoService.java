package io.electrica.webhook.service.dto;

import com.github.dozermapper.core.Mapper;
import io.electrica.common.context.IdentityContextHolder;
import io.electrica.common.helper.StringUtils;
import io.electrica.webhook.dto.CreateWebhookDto;
import io.electrica.webhook.dto.WebhookDto;
import io.electrica.webhook.model.Webhook;
import io.electrica.webhook.service.WebhookService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class WebhookDtoService {

    private final WebhookService webhookService;
    private final IdentityContextHolder identityContextHolder;
    private final Mapper mapper;
    private final String webhookBaseURL;

    public WebhookDtoService(WebhookService webhookService, IdentityContextHolder identityContextHolder, Mapper mapper,
                             @Value("${webhook.base-url}") String webhookBaseURL) {
        this.webhookService = webhookService;
        this.identityContextHolder = identityContextHolder;
        this.mapper = mapper;
        this.webhookBaseURL = webhookBaseURL;
    }

    public WebhookDto create(CreateWebhookDto dto) {
        Webhook webhook = webhookService.create(toCreateEntity(dto));
        WebhookDto webhookDto = toDto(webhook);
        return webhookDto;
    }

    public WebhookDto findById(UUID id) {
        Webhook entity = webhookService.findById(id);
        WebhookDto webhookDto = toDto(entity);
        return webhookDto;
    }

    public List<WebhookDto> findAllByConnectionId(Long connectionId) {
        return toDto(webhookService.findByConnectionId(connectionId));
    }

    public void delete(UUID id) {
        webhookService.delete(id);
    }

    private Webhook toCreateEntity(CreateWebhookDto dto) {
        return mapper.map(dto, Webhook.class);
    }

    private WebhookDto toDto(Webhook entity) {
        WebhookDto dto = mapper.map(entity, WebhookDto.class);
        dto.setUrl(generateWebhookURL(entity));
        return dto;
    }

    private List<WebhookDto> toDto(List<Webhook> fromList) {
        return fromList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private String generateWebhookURL(Webhook entity) {
        return webhookBaseURL + StringUtils.URL_SLASH + entity.getId() + StringUtils.URL_SLASH + entity.getHash();
    }
}
