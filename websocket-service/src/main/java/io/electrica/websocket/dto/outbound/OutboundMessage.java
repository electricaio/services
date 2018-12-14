package io.electrica.websocket.dto.outbound;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

import java.util.UUID;

@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = WebhookOutboundMessage.class, name = WebhookOutboundMessage.TYPE)
})
public abstract class OutboundMessage {

    private final UUID id;

    OutboundMessage() {
        this.id = UUID.randomUUID();
    }
}
