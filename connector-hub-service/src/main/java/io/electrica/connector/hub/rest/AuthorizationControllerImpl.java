package io.electrica.connector.hub.rest;

import io.electrica.connector.hub.rest.dto.CreateBasicAuthorizationDto;
import io.electrica.connector.hub.rest.dto.CreateTokenAuthorizationDto;
import io.electrica.connector.hub.rest.dto.ReadAuthorizationDto;
import io.electrica.connector.hub.service.AuthorizationDtoService;
import io.electrica.connector.hub.service.BasicAuthorizationDtoService;
import io.electrica.connector.hub.service.TokenAuthorizationDtoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class AuthorizationControllerImpl implements AuthorizationController {

    private final AuthorizationDtoService authorizationDtoService;
    private final TokenAuthorizationDtoService tokenAuthorizationDtoService;
    private final BasicAuthorizationDtoService basicAuthorizationDtoService;

    public AuthorizationControllerImpl(AuthorizationDtoService authorizationDtoService,
                                       TokenAuthorizationDtoService tokenAuthorizationDtoService,
                                       BasicAuthorizationDtoService basicAuthorizationDtoService) {
        this.authorizationDtoService = authorizationDtoService;
        this.tokenAuthorizationDtoService = tokenAuthorizationDtoService;
        this.basicAuthorizationDtoService = basicAuthorizationDtoService;
    }

    @Override
    @PreAuthorize("#common.hasPermission('ReadActiveConnection') AND #connection.canUserAccess(#connectionId)")
    public ResponseEntity<List<ReadAuthorizationDto>> findAll(Long connectionId) {
        final List<ReadAuthorizationDto> dto = authorizationDtoService.findAllByConnectionId(connectionId);
        return ResponseEntity.ok().body(dto);
    }

    @Override
    @PreAuthorize("#common.hasPermission('AssociateAccessKeyToConnector') " +
            "AND #connection.canUserAccess(#connectionId)")
    public ResponseEntity<ReadAuthorizationDto> authorizeWithUserAndPassword(Long connectionId,
                                                                             @Valid @RequestBody
            CreateBasicAuthorizationDto request) {
        final ReadAuthorizationDto dto = basicAuthorizationDtoService.create(connectionId, request);
        return ResponseEntity.ok().body(dto);
    }

    @Override
    @PreAuthorize("#common.hasPermission('AssociateAccessKeyToConnector') " +
            "AND #connection.canUserAccess(#connectionId)")
    public ResponseEntity<ReadAuthorizationDto> authorizeWithToken(Long connectionId, @Valid @RequestBody
            CreateTokenAuthorizationDto request) {
        final ReadAuthorizationDto dto = tokenAuthorizationDtoService.create(connectionId, request);
        return ResponseEntity.ok().body(dto);
    }
}
