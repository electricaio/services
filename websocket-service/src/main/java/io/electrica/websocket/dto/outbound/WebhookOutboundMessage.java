package io.electrica.websocket.dto.outbound;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.electrica.webhook.message.WebhookMessage;
import lombok.Getter;

@Getter
@JsonTypeName(WebhookOutboundMessage.TYPE)
public class WebhookOutboundMessage extends OutboundMessage {

    public static final String TYPE = "webhook";

    private final WebhookMessage data;

    public WebhookOutboundMessage(WebhookMessage data) {
        this.data = data;
    }
}
