package io.electrica.connector.hub.service.dto;

import io.electrica.connector.hub.dto.*;
import io.electrica.connector.hub.dto.sdk.*;
import io.electrica.connector.hub.model.*;
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
    private final IbmAuthorizationDtoService ibmAuthorizationDtoService;

    @Inject
    public FullConnectionDtoService(
            ConnectionService connectionService,
            ConnectionDtoService connectionDtoService,
            ConnectorDtoService connectorDtoService,
            BasicAuthorizationDtoService basicAuthorizationDtoService,
            TokenAuthorizationDtoService tokenAuthorizationDtoService,
            IbmAuthorizationDtoService ibmAuthorizationDtoService
    ) {
        this.connectionService = connectionService;
        this.connectionDtoService = connectionDtoService;
        this.connectorDtoService = connectorDtoService;
        this.basicAuthorizationDtoService = basicAuthorizationDtoService;
        this.tokenAuthorizationDtoService = tokenAuthorizationDtoService;
        this.ibmAuthorizationDtoService = ibmAuthorizationDtoService;
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
        } else if (authorization instanceof IbmAuthorization) {
            IbmAuthorizationDto data = ibmAuthorizationDtoService.toDto((IbmAuthorization) authorization);
            return new IbmTypedAuthorizationDto(data);
        } else if (authorization == null) {
            return null;
        }

        String type = authorization.getClass().getName();
        throw new UnsupportedOperationException("Unsupported authorization type: " + type);
    }
}
