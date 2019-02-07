package io.electrica.invoker.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.electrica.connector.dto.ConnectorExecutorContext;
import io.electrica.connector.dto.ConnectorExecutorResult;
import io.electrica.connector.dto.ErrorDto;
import io.electrica.connector.dto.InvocationContext;
import io.electrica.connector.feign.ConnectorExecutorClient;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.ConnectorDto;
import io.electrica.connector.hub.dto.sdk.FullConnectionDto;
import io.electrica.connector.hub.feign.ConnectionClient;
import io.electrica.invoker.dto.TinyConnectionDto;
import io.electrica.metric.common.mq.connection.invocation.ConnectionInvocationEventSender;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Component
public class InvokerService {

    private final ConnectionClient connectionClient;
    private final ConnectorExecutorClient connectorExecutorClient;
    private final ConnectionInvocationEventSender eventSender;

    @Inject
    public InvokerService(ConnectionClient connectionClient, ConnectorExecutorClient connectorExecutorClient,
                          ConnectionInvocationEventSender eventSender) {
        this.connectionClient = connectionClient;
        this.connectorExecutorClient = connectorExecutorClient;
        this.eventSender = eventSender;
    }

    public ConnectorExecutorResult invokeSync(InvocationContext context) {
        Long connectionId = context.getConnectionId();
        FullConnectionDto connection = connectionClient.getFull(connectionId).getBody();
        UUID invocationId = createInvocationId();
        LocalDateTime startTime = LocalDateTime.now();
        sendConnectionInvocation(startTime, invocationId, context, connection);
        ConnectorExecutorResult result = connectorExecutorClient.executeSync(new ConnectorExecutorContext(
                invocationId,
                context,
                connection
        )).getBody();
        sendConnectionInvocationResult(startTime, connection, result);
        return result;
    }

    private void sendConnectionInvocation(LocalDateTime startTime, UUID invocationId, InvocationContext context,
                                          FullConnectionDto connection) {
        ConnectionDto connectionDto = connection == null ? new ConnectionDto() : connection.getConnection();
        ConnectorDto connectorDto = connection == null ? new ConnectorDto() : connection.getConnector();
        eventSender.sendConnectionInvocation(
                invocationId,
                context.getInstanceId(),
                startTime,
                connectionDto.getId(),
                connectionDto.getRevisionVersion(),
                connectionDto.getAuthorizationId(),
                connectorDto.getId(),
                connectorDto.getRevisionVersion(),
                connectorDto.getErn(),
                context.getAction(),
                context.getParameters(),
                context.getPayload()
        );
    }

    private void sendConnectionInvocationResult(LocalDateTime startTime, FullConnectionDto connection,
                                                ConnectorExecutorResult result) {
        if (result != null) {
            ConnectionDto connectionDto = connection.getConnection();
            ConnectorDto connectorDto = connection.getConnector();
            ErrorDto error = result.getError() == null ? new ErrorDto() : result.getError();
            eventSender.sendConnectionInvocationResult(
                    result.getInvocationId(),
                    result.getInstanceId(),
                    startTime,
                    LocalDateTime.now(),
                    connectionDto.getId(),
                    connectionDto.getRevisionVersion(),
                    connectionDto.getAuthorizationId(),
                    connectorDto.getId(),
                    connectorDto.getRevisionVersion(),
                    connectorDto.getErn(),
                    result.getSuccess(),
                    result.getResult(),
                    error.getCode(),
                    error.getMessage(),
                    error.getStackTrace(),
                    error.getPayload()
            );
        }
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
