package io.electrica.webhook.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class MessageResultDto {

    @NotNull
    private UUID messageId;

    @NotNull
    private UUID webhookId;

    @NotNull
    private UUID sdkInstanceId;

    @NotNull
    private UUID webhookServiceId;

    private JsonNode payload;

}
