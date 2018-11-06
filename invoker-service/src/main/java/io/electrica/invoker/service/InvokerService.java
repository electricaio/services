package io.electrica.invoker.service;

import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.sdk.FullConnectionDto;
import io.electrica.connector.hub.feign.ConnectionClient;
import io.electrica.invoker.dto.InvocationContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component
public class InvokerService {

    private final ConnectionClient connectionClient;

    @Inject
    public InvokerService(ConnectionClient connectionClient) {
        this.connectionClient = connectionClient;
    }

    public Object invokeSync(InvocationContext context) {
        FullConnectionDto connection = connectionClient.getFull(context.getConnectionId()).getBody();
        // TODO invoke connector service
        return connection;
    }

    public List<ConnectionDto> getConnection(String connectionName) {
        return connectionClient.findAllByMe(connectionName).getBody();
    }
}
