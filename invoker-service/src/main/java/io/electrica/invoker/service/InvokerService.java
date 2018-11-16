package io.electrica.invoker.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.electrica.connector.dto.ConnectorExecutorContext;
import io.electrica.connector.dto.ConnectorExecutorResult;
import io.electrica.connector.dto.InvocationContext;
import io.electrica.connector.feign.ConnectorExecutorClient;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.sdk.FullConnectionDto;
import io.electrica.connector.hub.feign.ConnectionClient;
import io.electrica.invoker.dto.TinyConnectionDto;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

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

    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public List<TinyConnectionDto> findConnections(String connectionName, String ern) {
        List<ConnectionDto> connections = connectionClient.findAllByAccessKey(connectionName, ern).getBody();
        return requireNonNull(connections)
                .stream()
                .map(c -> new TinyConnectionDto(c.getId(), c.getName(), c.getProperties()))
                .collect(Collectors.toList());
    }

}
