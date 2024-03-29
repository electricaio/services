package io.electrica.connector.hub.rest;

import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.CreateConnectionDto;
import io.electrica.connector.hub.dto.UpdateConnectionDto;
import io.electrica.connector.hub.dto.sdk.FullConnectionDto;
import io.electrica.connector.hub.service.dto.ConnectionDtoService;
import io.electrica.connector.hub.service.dto.FullConnectionDtoService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ConnectionControllerImpl implements ConnectionController {

    private final ConnectionDtoService connectionDtoService;
    private final FullConnectionDtoService fullConnectionDtoService;

    public ConnectionControllerImpl(
            ConnectionDtoService connectionDtoService,
            FullConnectionDtoService fullConnectionDtoService
    ) {
        this.connectionDtoService = connectionDtoService;
        this.fullConnectionDtoService = fullConnectionDtoService;
    }

    @Override
    @PreAuthorize("#common.isUser(#userId) AND #common.hasPermission('ReadActiveConnection')")
    public ResponseEntity<List<ConnectionDto>> findAllByUser(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "connectorId", required = false) @Nullable Long connectorId) {
        final List<ConnectionDto> result = connectionDtoService.findAllByUser(userId, connectorId);
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
    @PreAuthorize("#common.hasPermission('UpdateConnection') AND #connection.canUserAccess(#id)")
    public ResponseEntity<ConnectionDto> update(@PathVariable("id") Long id,
                                                @Valid @RequestBody UpdateConnectionDto dto) {
        final ConnectionDto result = connectionDtoService.update(id, dto);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#common.hasPermission('ReadActiveConnection') AND #connection.canUserAccess(#id)")
    public ResponseEntity<ConnectionDto> get(@PathVariable("id") Long id) {
        ConnectionDto result = connectionDtoService.findById(id, true);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#oauth2.hasScope('sdk')")
    @PostAuthorize("#connection.isAccessKey(returnObject.getBody().getConnection().getAccessKeyId())")
    public ResponseEntity<FullConnectionDto> getFull(@PathVariable("id") Long id) {
        FullConnectionDto result = fullConnectionDtoService.findById(id);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#oauth2.hasScope('sdk')")
    public ResponseEntity<List<ConnectionDto>> findAllByAccessKey(
            @RequestParam(value = "connectionName", required = false) @Nullable String connectionName,
            @RequestParam(value = "ern") String ern
    ) {
        final List<ConnectionDto> result = connectionDtoService.findAllByAccessKey(connectionName, ern);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#common.hasPermission('ReadActiveConnection') AND #connection.accessKeyBelongsUser(#accessKeyId)")
    public ResponseEntity<List<ConnectionDto>> findAllByAccessKeyId(@PathVariable("accessKeyId") Long accessKeyId) {
        final List<ConnectionDto> result = connectionDtoService.findAllByAccessKeyId(accessKeyId);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#common.hasPermission('ReadActiveConnection') AND #connection.canUserAccess(#connectionId)")
    public ResponseEntity<Boolean> connectionBelongsCurrentUser(@PathVariable("connectionId") Long connectionId) {
        return ResponseEntity.ok(true);
    }

    @Override
    @PreAuthorize("#common.hasPermission('DeActivateConnection') AND #connection.canUserAccess(#connectionId)")
    public void delete(@PathVariable("connectionId") Long connectionId) {
        connectionDtoService.delete(connectionId);
    }
}
