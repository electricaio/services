package io.electrica.invoker.service;

import io.electrica.connector.dto.ConnectorExecutorContext;
import io.electrica.connector.dto.ConnectorExecutorResult;
import io.electrica.connector.feign.ConnectorExecutorClient;
import io.electrica.connector.hub.dto.sdk.FullConnectionDto;
import io.electrica.connector.hub.feign.ConnectionClient;
import io.electrica.connector.dto.InvocationContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.UUID;

@Component
public class InvokerService {

    private final ConnectionClient connectionClient;
    private final ConnectorExecutorClient connectorExecutorClient;

    @Inject
    public InvokerService(ConnectionClient connectionClient, ConnectorExecutorClient connectorExecutorClient) {
        this.connectionClient = connectionClient;
        this.connectorExecutorClient = connectorExecutorClient;
    }

    public ConnectorExecutorResult invokeSync(InvocationContext context) {
        Long connectionId = context.getConnectionId();
        FullConnectionDto connection = connectionClient.getFull(connectionId).getBody();
        return connectorExecutorClient.executeSync(new ConnectorExecutorContext(
                createInvocationId(),
                context,
                connection
        )).getBody();
    }

    private UUID createInvocationId() {
        return UUID.randomUUID();
    }
}
