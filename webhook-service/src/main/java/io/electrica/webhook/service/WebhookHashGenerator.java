package io.electrica.webhook.service;

import io.electrica.common.helper.StringUtils;
import io.electrica.webhook.model.Webhook;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.Base64;

@Component
public class WebhookHashGenerator {

    public String generateHash(Webhook entity) {
        StringBuilder inputString = new StringBuilder();
        inputString.append(entity.getConnectorId()).append(StringUtils.COLON).append(entity.getConnectorId())
                .append(StringUtils.COLON).append(entity.getOrganizationId()).append(StringUtils.COLON)
                .append(entity.getUserId());
        return Base64.getEncoder().withoutPadding().encodeToString(inputString.toString()
                .getBytes(Charset.forName("UTF-8")));
    }
}
