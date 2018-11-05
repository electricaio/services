package io.electrica.connector.hub.rest;

import io.electrica.connector.hub.dto.BasicAuthorizationDto;
import io.electrica.connector.hub.dto.CreateBasicAuthorizationDto;
import io.electrica.connector.hub.dto.CreateTokenAuthorizationDto;
import io.electrica.connector.hub.dto.TokenAuthorizationDto;
import io.electrica.connector.hub.service.dto.BasicAuthorizationDtoService;
import io.electrica.connector.hub.service.dto.TokenAuthorizationDtoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AuthorizationControllerImpl implements AuthorizationController {

    private final TokenAuthorizationDtoService tokenAuthorizationDtoService;
    private final BasicAuthorizationDtoService basicAuthorizationDtoService;

    public AuthorizationControllerImpl(
            TokenAuthorizationDtoService tokenAuthorizationDtoService,
            BasicAuthorizationDtoService basicAuthorizationDtoService
    ) {
        this.tokenAuthorizationDtoService = tokenAuthorizationDtoService;
        this.basicAuthorizationDtoService = basicAuthorizationDtoService;
    }

    @Override
    @PreAuthorize("#common.hasPermission('AssociateAccessKeyToConnector') " +
            "AND #connection.canUserAccess(#connectionId)")
    public ResponseEntity<BasicAuthorizationDto> authorizeWithBasic(
            @PathVariable("connectionId") Long connectionId,
            @Valid @RequestBody CreateBasicAuthorizationDto dto
    ) {
        BasicAuthorizationDto result = basicAuthorizationDtoService.createAndAssignToConnection(connectionId, dto);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#common.hasPermission('AssociateAccessKeyToConnector') " +
            "AND #connection.canUserAccess(#connectionId)")
    public ResponseEntity<TokenAuthorizationDto> authorizeWithToken(
            @PathVariable("connectionId") Long connectionId,
            @Valid @RequestBody CreateTokenAuthorizationDto dto
    ) {
        TokenAuthorizationDto result = tokenAuthorizationDtoService.createAndAssignToConnection(connectionId, dto);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#connection.canUserAccessAuthorization(#id)")
    public ResponseEntity<BasicAuthorizationDto> getBasic(@PathVariable("id") Long id) {
        BasicAuthorizationDto result = basicAuthorizationDtoService.findById(id, true);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#connection.canUserAccessAuthorization(#id)")
    public ResponseEntity<TokenAuthorizationDto> getToken(@PathVariable("id") Long id) {
        TokenAuthorizationDto result = tokenAuthorizationDtoService.findById(id, true);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#connection.canUserAccessAuthorization(#id)")
    public ResponseEntity<BasicAuthorizationDto> updateBasic(
            @PathVariable("id") Long id,
            @Valid @RequestBody BasicAuthorizationDto dto
    ) {
        BasicAuthorizationDto result = basicAuthorizationDtoService.update(id, dto);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#connection.canUserAccessAuthorization(#id)")
    public ResponseEntity<TokenAuthorizationDto> updateToken(
            @PathVariable("id") Long id,
            @Valid @RequestBody TokenAuthorizationDto dto
    ) {
        TokenAuthorizationDto result = tokenAuthorizationDtoService.update(id, dto);
        return ResponseEntity.ok(result);
    }

}
