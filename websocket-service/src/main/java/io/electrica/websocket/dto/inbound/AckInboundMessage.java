package io.electrica.websocket.dto.inbound;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@JsonTypeName(AckInboundMessage.TYPE)
public class AckInboundMessage extends InboundMessage {

    public static final String TYPE = "ack";

    @NotNull
    private UUID correlationId;
    private Map<String, String> arguments;

}
