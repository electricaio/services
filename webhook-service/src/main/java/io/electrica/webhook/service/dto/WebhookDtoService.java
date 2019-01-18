package io.electrica.webhook.service.dto;

import com.github.dozermapper.core.Mapper;
import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityContextHolder;
import io.electrica.webhook.dto.*;
import io.electrica.webhook.helper.SignHelper;
import io.electrica.webhook.model.Webhook;
import io.electrica.webhook.service.WebhookService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.electrica.webhook.rest.WebhookController.*;

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
        return setUrls(entity, dto);
    }

    private <T extends WebhookDto> T setUrls(Webhook entity, T dto) {
        dto.setSubmitUrl(getWebhookUrl(entity, false, SUBMIT_SUFFIX));
        dto.setInvokeUrl(getWebhookUrl(entity, false, INVOKE_SUFFIX));
        if (entity.getIsPublic()) {
            dto.setPublicSubmitUrl(getWebhookUrl(entity, true, SUBMIT_SUFFIX));
            dto.setPublicInvokeUrl(getWebhookUrl(entity, true, INVOKE_SUFFIX));
        }
        return dto;
    }

    private String getWebhookUrl(Webhook entity, boolean isPublic, String suffix) {
        String pathPrefix = isPublic ? PUBLIC_PREFIX : PREFIX;
        String sign = isPublic ? "/" + SignHelper.createSign(entity) : "";
        return webhookBaseURL + pathPrefix + "/" + entity.getId() + sign + suffix;
    }
}
