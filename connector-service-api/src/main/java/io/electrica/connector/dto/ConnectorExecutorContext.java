package io.electrica.connector.dto;

import com.google.common.base.MoreObjects;
import io.electrica.connector.hub.dto.sdk.FullConnectionDto;
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
public class ConnectorExecutorContext {

    @NotNull
    private UUID invocationId;

    @NotNull
    private InvocationContext invocationContext;

    @NotNull
    private FullConnectionDto connection;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("invocationId", invocationId)
                .add("instanceId", invocationContext.getInstanceId())
                .add("connectionId", invocationContext.getConnectionId())
                .add("parameters", invocationContext.getParameters().asText())
                .add("connectorId", connection.getConnector().getId())
                .toString();
    }
}
