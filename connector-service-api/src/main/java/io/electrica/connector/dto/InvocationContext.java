package io.electrica.connector.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class InvocationContext {

    @NotNull
    private UUID instanceId;

    @NotNull
    private Long connectionId;

    @NotNull
    private JsonNode parameters;

    private JsonNode payload;

}
