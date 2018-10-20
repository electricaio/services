package io.electrica.stl.rest.impl;

import io.electrica.stl.rest.ConnectionController;
import io.electrica.stl.rest.dto.ConnectDto;
import io.electrica.stl.rest.dto.ConnectionDto;
import io.electrica.stl.service.ConnectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ConnectionControllerImpl implements ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionControllerImpl(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @Override
    @PreAuthorize(" #common.hasPermission('AssociateAccessKeyToConnector') " +
            "AND #common.haveOneOfRoles('OrgUser','OrgAdmin','SuperAdmin')")
    public ResponseEntity<ConnectionDto> connect(@Valid @RequestBody ConnectDto request) {
        final ConnectionDto dto = connectionService.create(request);
        return ResponseEntity.ok().body(dto);
    }
}
