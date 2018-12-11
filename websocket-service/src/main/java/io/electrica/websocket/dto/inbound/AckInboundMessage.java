package io.electrica.websocket.dto.inbound;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class AckInboundMessage {

    @NotNull
    private UUID correlationId;
    @NotNull
    private Boolean accepted;

}
