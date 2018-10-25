package io.electrica.connector.hub.service;

import io.electrica.connector.hub.rest.dto.CreateTokenAuthorizationDto;
import io.electrica.connector.hub.rest.dto.ReadAuthorizationDto;
import org.springframework.stereotype.Component;

/**
 * User Dto Service implementation class for token based authorization.
 */
@Component
public class TokenAuthorizationDtoService {

    private final AuthorizationService authorizationService;

    public TokenAuthorizationDtoService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public ReadAuthorizationDto create(Long connectionId, CreateTokenAuthorizationDto request) {
        return authorizationService.createTokenAuth(connectionId, request);
    }

}
