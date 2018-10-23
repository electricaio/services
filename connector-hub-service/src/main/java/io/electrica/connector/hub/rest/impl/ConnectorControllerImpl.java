package io.electrica.connector.hub.rest.impl;

import io.electrica.connector.hub.rest.ConnectorController;
import io.electrica.connector.hub.rest.dto.CreateConnectorDto;
import io.electrica.connector.hub.rest.dto.ReadConnectorDto;
import io.electrica.connector.hub.service.ConnectorDtoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ConnectorControllerImpl implements ConnectorController {

    private final ConnectorDtoService connectorDtoService;

    public ConnectorControllerImpl(ConnectorDtoService connectorDtoService) {
        this.connectorDtoService = connectorDtoService;
    }

    @Override
    public ResponseEntity<List<ReadConnectorDto>> findAll() {
        final List<ReadConnectorDto> result = connectorDtoService.findAll();
        return ResponseEntity.ok().body(result);
    }

    @Override
    @PreAuthorize(" #common.hasPermission('CreateConnector') AND #common.isSuperAdmin() ")
    public ResponseEntity<ReadConnectorDto> create(@Valid @RequestBody CreateConnectorDto dto) {
        final ReadConnectorDto result = connectorDtoService.create(dto);
        return ResponseEntity.ok().body(result);
    }
}
