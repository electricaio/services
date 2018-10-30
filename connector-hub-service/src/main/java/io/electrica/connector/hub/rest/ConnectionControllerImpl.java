package io.electrica.connector.hub.rest;

import io.electrica.connector.hub.rest.dto.ConnectDto;
import io.electrica.connector.hub.rest.dto.ConnectionDto;
import io.electrica.connector.hub.service.ConnectionDtoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ConnectionControllerImpl implements ConnectionController {

    private final ConnectionDtoService connectionDtoService;

    public ConnectionControllerImpl(ConnectionDtoService connectionDtoService) {
        this.connectionDtoService = connectionDtoService;
    }

    @Override
    @PreAuthorize("#common.isUser(#userId) AND #common.hasPermission('ReadActiveConnection')")
    public ResponseEntity<List<ConnectionDto>> findAllByUser(Long userId) {
        final List<ConnectionDto> dto = connectionDtoService.findAllByUser(userId);
        return ResponseEntity.ok().body(dto);
    }

    @Override
    @PreAuthorize("#common.hasPermission('AssociateAccessKeyToConnector')")
    public ResponseEntity<ConnectionDto> connect(@Valid @RequestBody ConnectDto request) {
        final ConnectionDto dto = connectionDtoService.create(request);
        return ResponseEntity.ok().body(dto);
    }
}
