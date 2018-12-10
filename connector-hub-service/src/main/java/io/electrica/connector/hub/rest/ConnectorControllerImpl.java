package io.electrica.connector.hub.rest;

import io.electrica.connector.hub.dto.ConnectorDto;
import io.electrica.connector.hub.dto.CreateConnectorDto;
import io.electrica.connector.hub.service.dto.ConnectorDtoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
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
    @PreAuthorize(" #common.hasPermission('ReadConnector')")
    public ResponseEntity<List<ConnectorDto>> findAll() {
        final List<ConnectorDto> result = connectorDtoService.findAll();
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize(" #common.hasPermission('CreateConnector') AND #common.isSuperAdmin() ")
    public ResponseEntity<ConnectorDto> create(@Valid @RequestBody CreateConnectorDto dto) {
        final ConnectorDto result = connectorDtoService.create(dto);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize(" #common.hasPermission('ReadConnector')")
    public ResponseEntity<ConnectorDto> getConnector(@PathVariable Long id) {
        ConnectorDto connectorDto = connectorDtoService.findById(id, false);
        return ResponseEntity.ok(connectorDto);
    }
}
