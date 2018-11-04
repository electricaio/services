package io.electrica.connector.hub.rest;

import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.CreateConnectionDto;
import io.electrica.connector.hub.service.dto.ConnectionDtoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<List<ConnectionDto>> findAllByUser(@PathVariable("userId") Long userId) {
        final List<ConnectionDto> result = connectionDtoService.findAllByUser(userId);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#common.hasPermission('AssociateAccessKeyToConnector') AND " +
            "#connection.accessKeyBelongsUser(#request.getAccessKeyId())")
    public ResponseEntity<ConnectionDto> create(@Valid @RequestBody CreateConnectionDto request) {
        final ConnectionDto result = connectionDtoService.create(request);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#common.hasPermission('ReadActiveConnection') AND #connection.canUserAccess(#id)")
    public ResponseEntity<ConnectionDto> get(@PathVariable("id") Long id) {
        ConnectionDto result = connectionDtoService.findById(id, true);
        return ResponseEntity.ok(result);
    }
}
