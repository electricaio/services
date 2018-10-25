package io.electrica.connector.hub.service;

import io.electrica.connector.hub.rest.dto.CreateBasicAuthorizationDto;
import io.electrica.connector.hub.rest.dto.ReadAuthorizationDto;
import org.springframework.stereotype.Component;

/**
 * User Dto Service implementation class for basic authorization.
 */
@Component
public class BasicAuthorizationDtoService {

    private final AuthorizationService authorizationService;

    public BasicAuthorizationDtoService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public ReadAuthorizationDto create(Long connectionId, CreateBasicAuthorizationDto request) {
        return authorizationService.createBasicAuth(connectionId, request);
    }
}
