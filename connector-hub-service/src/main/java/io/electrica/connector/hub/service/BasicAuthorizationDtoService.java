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

    private final BasicAuthorizationService basicAuthorizationService;
    private final Mapper mapper;

    public BasicAuthorizationDtoService(BasicAuthorizationService basicAuthorizationService, Mapper mapper) {
        this.basicAuthorizationService = basicAuthorizationService;
        this.mapper = mapper;
    }

    public ReadAuthorizationDto create(Long connectionId, CreateBasicAuthorizationDto request) {
        final Authorization authorization = basicAuthorizationService.upsert(connectionId, request);
        return mapper.map(authorization, ReadAuthorizationDto.class);
    }
}
