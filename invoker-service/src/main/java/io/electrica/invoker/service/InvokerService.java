package io.electrica.invoker.service;

import com.github.dozermapper.core.Mapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.electrica.connector.dto.ConnectorExecutorContext;
import io.electrica.connector.dto.ConnectorExecutorResult;
import io.electrica.connector.feign.ConnectorExecutorClient;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.sdk.FullConnectionDto;
import io.electrica.connector.hub.feign.ConnectionClient;
import io.electrica.connector.dto.InvocationContext;
import io.electrica.invoker.dto.TinyConnectionDto;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class InvokerService {

    private final ConnectionClient connectionClient;
    private final ConnectorExecutorClient connectorExecutorClient;
    private final Mapper mapper;

    @Inject
    public InvokerService(ConnectionClient connectionClient, ConnectorExecutorClient connectorExecutorClient,
                          Mapper mapper) {
        this.connectionClient = connectionClient;
        this.connectorExecutorClient = connectorExecutorClient;
        this.mapper = mapper;
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

    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "Find a better way")
    public List<TinyConnectionDto> getConnection(String connectionName, String connector) {
        return toDto(connectionClient.findAllByAccessKey(connectionName, connector).getBody());
    }

    private List<TinyConnectionDto> toDto(List<ConnectionDto> fromList) {
        return fromList
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private io.electrica.invoker.dto.TinyConnectionDto toDto(ConnectionDto entity) {
        return mapper.map(entity, TinyConnectionDto.class);
    }
}
