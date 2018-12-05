package io.electrica.websocket.dto.outbound;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AckOutboundMessage.class, name = AckOutboundMessage.TYPE)
})
public abstract class OutboundMessage {

    @NotNull
    private UUID id;

}
