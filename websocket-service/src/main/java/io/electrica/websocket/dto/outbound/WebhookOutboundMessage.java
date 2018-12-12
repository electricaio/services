package io.electrica.websocket.dto.outbound;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.electrica.webhook.message.WebhookMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeName(WebhookOutboundMessage.TYPE)
public class WebhookOutboundMessage extends OutboundMessage {

    public static final String TYPE = "webhook";

    private WebhookMessage data;

}
