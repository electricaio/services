package io.electrica.connector.hub.service;

import com.github.dozermapper.core.Mapper;
import io.electrica.connector.hub.model.Authorization;
import io.electrica.connector.hub.rest.dto.CreateTokenAuthorizationDto;
import io.electrica.connector.hub.rest.dto.ReadAuthorizationDto;
import org.springframework.stereotype.Component;

/**
 * User Dto Service implementation class for token based authorization.
 */
@Component
public class TokenAuthorizationDtoService {

    private final TokenAuthorizationService tokenAuthorizationService;
    private final Mapper mapper;

    public TokenAuthorizationDtoService(TokenAuthorizationService tokenAuthorizationService, Mapper mapper) {
        this.tokenAuthorizationService = tokenAuthorizationService;
        this.mapper = mapper;
    }

    public ReadAuthorizationDto create(Long connectionId, CreateTokenAuthorizationDto request) {
        Authorization authorization = tokenAuthorizationService.upsert(connectionId, request);
        return mapper.map(authorization, ReadAuthorizationDto.class);
    }

}
