package io.electrica.websocket.dto.outbound;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@JsonTypeName(AckOutboundMessage.TYPE)
public class AckOutboundMessage extends OutboundMessage {

    public static final String TYPE = "ack";

    @NotNull
    private UUID correlationId;
    private Map<String, String> arguments;

}
