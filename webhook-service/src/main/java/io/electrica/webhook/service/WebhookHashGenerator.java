package io.electrica.webhook.service;

import io.electrica.common.helper.StringUtils;
import io.electrica.webhook.model.Webhook;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class WebhookHashGenerator {

    public String generateHash(Webhook entity) {
        String inputString = entity.getConnectorId() + StringUtils.COLON + entity.getConnectorId() + StringUtils.COLON +
                entity.getOrganizationId() + StringUtils.COLON + entity.getUserId();
        return Base64.getEncoder().withoutPadding().encodeToString(inputString.getBytes());
    }
}
