package io.electrica.webhook.service.dto;

import com.github.dozermapper.core.Mapper;
import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityContextHolder;
import io.electrica.common.helper.StringUtils;
import io.electrica.webhook.dto.ConnectionCreateWebhookDto;
import io.electrica.webhook.dto.ConnectionWebhookDto;
import io.electrica.webhook.dto.CreateWebhookDto;
import io.electrica.webhook.dto.WebhookScope;
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

    public WebhookDtoService(
            WebhookService webhookService,
            IdentityContextHolder identityContextHolder,
            Mapper mapper,
            @Value("${webhook.base-url}") String webhookBaseURL
    ) {
        this.webhookService = webhookService;
        this.identityContextHolder = identityContextHolder;
        this.mapper = mapper;
        this.webhookBaseURL = webhookBaseURL;
    }

    public ConnectionWebhookDto createConnection(ConnectionCreateWebhookDto dto) {
        Webhook entity = toCreateEntity(dto, WebhookScope.Connection);
        Webhook webhook = webhookService.create(entity);
        return toConnectionDto(webhook);
    }

    public List<ConnectionWebhookDto> findAllByConnectionId(Long connectionId) {
        return webhookService.findByConnectionId(connectionId).stream()
                .map(this::toConnectionDto)
                .collect(Collectors.toList());
    }

    private Webhook toCreateEntity(CreateWebhookDto dto, WebhookScope scope) {
        Webhook result = mapper.map(dto, Webhook.class);
        result.setScope(scope);

        Identity identity = identityContextHolder.getIdentity();
        result.setOrganizationId(identity.getOrganizationId());
        result.setUserId(identity.getUserId());
        return result;
    }

    public void delete(UUID id) {
        webhookService.delete(id);
    }

    private ConnectionWebhookDto toConnectionDto(Webhook entity) {
        ConnectionWebhookDto dto = mapper.map(entity, ConnectionWebhookDto.class);
        dto.setUrl(getWebhookURL(entity));
        return dto;
    }

    private String getWebhookURL(Webhook entity) {
        return webhookBaseURL + StringUtils.URL_SLASH + entity.getId();
    }
}
