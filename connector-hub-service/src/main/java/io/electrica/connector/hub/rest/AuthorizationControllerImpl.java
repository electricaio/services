package io.electrica.connector.hub.rest;

import io.electrica.connector.hub.dto.*;
import io.electrica.connector.hub.service.dto.BasicAuthorizationDtoService;
import io.electrica.connector.hub.service.dto.IbmAuthorizationDtoService;
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
    private final IbmAuthorizationDtoService ibmAuthorizationDtoService;

    public AuthorizationControllerImpl(
            TokenAuthorizationDtoService tokenAuthorizationDtoService,
            BasicAuthorizationDtoService basicAuthorizationDtoService,
            IbmAuthorizationDtoService ibmAuthorizationDtoService
    ) {
        this.tokenAuthorizationDtoService = tokenAuthorizationDtoService;
        this.basicAuthorizationDtoService = basicAuthorizationDtoService;
        this.ibmAuthorizationDtoService = ibmAuthorizationDtoService;
    }

    @Override
    @PreAuthorize("" +
            "#connection.hasPermission('AssociateAccessKeyToConnector') and " +
            "#connection.canUserAccess(#connectionId)"
    )
    public ResponseEntity<BasicAuthorizationDto> authorizeWithBasic(
            @PathVariable("connectionId") Long connectionId,
            @Valid @RequestBody CreateBasicAuthorizationDto dto
    ) {
        BasicAuthorizationDto result = basicAuthorizationDtoService.createAndAssignToConnection(connectionId, dto);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("" +
            "#connection.hasPermission('AssociateAccessKeyToConnector') and " +
            "#connection.canUserAccess(#connectionId)"
    )
    public ResponseEntity<TokenAuthorizationDto> authorizeWithToken(
            @PathVariable("connectionId") Long connectionId,
            @Valid @RequestBody CreateTokenAuthorizationDto dto
    ) {
        TokenAuthorizationDto result = tokenAuthorizationDtoService.createAndAssignToConnection(connectionId, dto);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("" +
            "#connection.hasPermission('AssociateAccessKeyToConnector') and " +
            "#connection.canUserAccess(#connectionId)"
    )
    public ResponseEntity<IbmAuthorizationDto> authorizeWithIbm(
            @PathVariable("connectionId") Long connectionId,
            @RequestBody CreateIbmAuthorizationDto dto
    ) {
        IbmAuthorizationDto result = ibmAuthorizationDtoService.createAndAssignToConnection(connectionId, dto);
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
    public ResponseEntity<IbmAuthorizationDto> getIbm(@PathVariable("id") Long id) {
        IbmAuthorizationDto result = ibmAuthorizationDtoService.findById(id, true);
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

    @Override
    @PreAuthorize("#connection.canUserAccessAuthorization(#id)")
    public ResponseEntity<IbmAuthorizationDto> updateIbm(
            @PathVariable("id") Long id,
            @RequestBody IbmAuthorizationDto dto
    ) {
        IbmAuthorizationDto result = ibmAuthorizationDtoService.update(id, dto);
        return ResponseEntity.ok(result);
    }

}
