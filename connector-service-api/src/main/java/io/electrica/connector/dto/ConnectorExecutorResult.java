package io.electrica.connector.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
