package io.electrica.connector.hub.service;

import com.github.dozermapper.core.Mapper;
import io.electrica.connector.hub.model.Authorization;
import io.electrica.connector.hub.rest.dto.CreateBasicAuthorizationDto;
import io.electrica.connector.hub.rest.dto.ReadAuthorizationDto;
import org.springframework.stereotype.Component;

/**
 * User Dto Service implementation class for basic authorization.
 */
@Component
public class BasicAuthorizationDtoService {

    private final AuthorizationService authorizationService;
    private final Mapper mapper;

    public BasicAuthorizationDtoService(AuthorizationService authorizationService, Mapper mapper) {
        this.authorizationService = authorizationService;
        this.mapper = mapper;
    }

    public ReadAuthorizationDto create(Long connectionId, CreateBasicAuthorizationDto request) {
        Authorization authorization = authorizationService.createBasicAuth(connectionId, request);
        return mapper.map(authorization, ReadAuthorizationDto.class);
    }
}
