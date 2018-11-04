package io.electrica.connector.hub.service.dto;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.dto.AbstractDtoService;
import io.electrica.connector.hub.dto.CreateTokenAuthorizationDto;
import io.electrica.connector.hub.dto.TokenAuthorizationDto;
import io.electrica.connector.hub.model.TokenAuthorization;
import io.electrica.connector.hub.service.TokenAuthorizationService;
import org.springframework.stereotype.Component;

/**
 * User Dto Service implementation class for token based authorization.
 */
@Component
public class TokenAuthorizationDtoService extends
        AbstractDtoService<TokenAuthorization, CreateTokenAuthorizationDto, TokenAuthorizationDto> {

    private final TokenAuthorizationService tokenAuthorizationService;

    public TokenAuthorizationDtoService(TokenAuthorizationService tokenAuthorizationService) {
        this.tokenAuthorizationService = tokenAuthorizationService;
    }

    public TokenAuthorizationDto createAndAssignToConnection(Long connectionId, CreateTokenAuthorizationDto dto) {
        return toDto(tokenAuthorizationService.createAndAssignToConnection(connectionId, toCreateEntity(dto)));
    }

    @Override
    protected AbstractService<TokenAuthorization> getService() {
        return tokenAuthorizationService;
    }

    @Override
    protected Class<TokenAuthorization> getEntityClass() {
        return TokenAuthorization.class;
    }

    @Override
    protected Class<TokenAuthorizationDto> getDtoClass() {
        return TokenAuthorizationDto.class;
    }
}
