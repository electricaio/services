package io.electrica.connector.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class ConnectorExecutorResult {

    @NotNull
    private UUID invocationId;

    @NotNull
    private UUID instanceId;

    @NotNull
    private Long connectionId;

    @NotNull
    private Boolean success;

    private JsonNode result;

    private ErrorDto error;

}
