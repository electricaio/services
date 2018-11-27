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
        webhookDto.setUrl(generateWebhookURL(webhook));
        return webhookDto;
    }

    public Webhook toCreateEntity(CreateWebhookDto dto) {
        return mapper.map(dto, Webhook.class);
    }

    public WebhookDto toDto(Webhook entity) {
        return mapper.map(entity, WebhookDto.class);
    }

    private String generateWebhookURL(Webhook entity) {
        return webhookBaseURL + entity.getId() + StringUtils.URL_SLASH + entity.getHash();
    }
}
