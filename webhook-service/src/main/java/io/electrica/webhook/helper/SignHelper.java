package io.electrica.webhook.helper;

import io.electrica.webhook.model.Webhook;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public class SignHelper {

    private static final String SIGN_TEMPLATE = "%d:%d:%d";

    private SignHelper() {
    }

    public static String createSign(Webhook webhook) {
        String data = String.format(
                SIGN_TEMPLATE,
                webhook.getOrganizationId(),
                webhook.getUserId(),
                webhook.getAccessKeyId()
        );
        return Base64.getUrlEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    public static boolean checkSign(Webhook webhook, String sign) {
        return Objects.equals(sign, createSign(webhook));
    }
}
