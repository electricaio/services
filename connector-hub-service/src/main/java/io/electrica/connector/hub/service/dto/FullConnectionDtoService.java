package io.electrica.connector.hub.service.dto;

import io.electrica.connector.hub.dto.BasicAuthorizationDto;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.ConnectorDto;
import io.electrica.connector.hub.dto.TokenAuthorizationDto;
import io.electrica.connector.hub.dto.sdk.BasicTypedAuthorizationDto;
import io.electrica.connector.hub.dto.sdk.FullConnectionDto;
import io.electrica.connector.hub.dto.sdk.TokenTypedAuthorizationDto;
import io.electrica.connector.hub.dto.sdk.TypedAuthorizationDto;
import io.electrica.connector.hub.model.Authorization;
import io.electrica.connector.hub.model.BasicAuthorization;
import io.electrica.connector.hub.model.Connection;
import io.electrica.connector.hub.model.TokenAuthorization;
import io.electrica.connector.hub.service.ConnectionService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class FullConnectionDtoService {

    private final ConnectionService connectionService;
    private final ConnectionDtoService connectionDtoService;
    private final ConnectorDtoService connectorDtoService;
    private final BasicAuthorizationDtoService basicAuthorizationDtoService;
    private final TokenAuthorizationDtoService tokenAuthorizationDtoService;

    @Inject
    public FullConnectionDtoService(
            ConnectionService connectionService,
            ConnectionDtoService connectionDtoService,
            ConnectorDtoService connectorDtoService,
            BasicAuthorizationDtoService basicAuthorizationDtoService,
            TokenAuthorizationDtoService tokenAuthorizationDtoService
    ) {
        this.connectionService = connectionService;
        this.connectionDtoService = connectionDtoService;
        this.connectorDtoService = connectorDtoService;
        this.basicAuthorizationDtoService = basicAuthorizationDtoService;
        this.tokenAuthorizationDtoService = tokenAuthorizationDtoService;
    }

    public FullConnectionDto findById(Long connectionId) {
        Connection connection = connectionService.findByIdWithConnectorAndAuthorization(connectionId);
        ConnectionDto connectionDto = connectionDtoService.toDto(connection);
        ConnectorDto connectorDto = connectorDtoService.toDto(connection.getConnector());
        TypedAuthorizationDto authorizationDto = getAuthorizationDto(connection);

        return new FullConnectionDto(connectionDto, connectorDto, authorizationDto);
    }

    private TypedAuthorizationDto getAuthorizationDto(Connection connection) {
        Authorization authorization = connection.getAuthorization();
        if (authorization instanceof BasicAuthorization) {
            BasicAuthorizationDto data = basicAuthorizationDtoService.toDto((BasicAuthorization) authorization);
            return new BasicTypedAuthorizationDto(data);
        } else if (authorization instanceof TokenAuthorization) {
            TokenAuthorizationDto data = tokenAuthorizationDtoService.toDto((TokenAuthorization) authorization);
            return new TokenTypedAuthorizationDto(data);
        }

        String type = authorization.getClass().getName();
        throw new UnsupportedOperationException("Unsupported authorization type: " + type);
    }
}
