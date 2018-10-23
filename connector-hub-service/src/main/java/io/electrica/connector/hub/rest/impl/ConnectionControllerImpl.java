package io.electrica.connector.hub.rest.impl;

import io.electrica.connector.hub.rest.ConnectionController;
import io.electrica.connector.hub.rest.dto.ConnectDto;
import io.electrica.connector.hub.rest.dto.ConnectionDto;
import io.electrica.connector.hub.rest.dto.CreateBasicAuthorizationDto;
import io.electrica.connector.hub.rest.dto.ReadAuthorizationDto;
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
    @PreAuthorize("#common.hasPermission('AssociateAccessKeyToConnector')")
    public ResponseEntity<ReadAuthorizationDto> authorize(Long connectionId, CreateBasicAuthorizationDto request) {
        final ReadAuthorizationDto dto = authorizationService.createBasicAuth(connectionId, request);
        return ResponseEntity.ok().body(dto);
    }
}
