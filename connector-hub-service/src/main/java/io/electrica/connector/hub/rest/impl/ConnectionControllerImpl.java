package io.electrica.connector.hub.rest.impl;

import io.electrica.connector.hub.rest.ConnectionController;
import io.electrica.connector.hub.rest.dto.*;
import io.electrica.connector.hub.service.AuthorizationService;
import io.electrica.connector.hub.service.ConnectionDtoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ConnectionControllerImpl implements ConnectionController {

    private final ConnectionDtoService connectionDtoService;

    private final AuthorizationService authorizationService;

    public ConnectionControllerImpl(
            ConnectionDtoService connectionDtoService,
            AuthorizationService authorizationService) {
        this.connectionDtoService = connectionDtoService;
        this.authorizationService = authorizationService;
    }

    @Override
    @PreAuthorize("#common.hasPermission('AssociateAccessKeyToConnector')")
    public ResponseEntity<ConnectionDto> connect(@Valid @RequestBody ConnectDto request) {
        final ConnectionDto dto = connectionDtoService.create(request);
        return ResponseEntity.ok().body(dto);
    }

    @Override
    @PreAuthorize("#common.hasPermission('AssociateAccessKeyToConnector') " +
            "AND #connection.canUserAccess(#connectionId)")
    public ResponseEntity<ReadAuthorizationDto> authorizeWithUserAndPassword(Long connectionId, @Valid @RequestBody
            CreateBasicAuthorizationDto request) {
        final ReadAuthorizationDto dto = authorizationService.createBasicAuth(connectionId, request);
        return ResponseEntity.ok().body(dto);
    }

    @Override
    @PreAuthorize("#common.hasPermission('AssociateAccessKeyToConnector') " +
            "AND #connection.canUserAccess(#connectionId)")
    public ResponseEntity<ReadAuthorizationDto> authorizeWithToken(Long connectionId,  @Valid @RequestBody
            CreateTokenAuthorizationDto request) {
        final ReadAuthorizationDto dto = authorizationService.createTokenAuth(connectionId, request);
        return ResponseEntity.ok().body(dto);
    }
}
