package io.electrica.connector.hub.service;

import io.electrica.connector.hub.model.Authorization;
import io.electrica.connector.hub.model.TokenAuthorization;
import io.electrica.connector.hub.model.enums.AuthorizationTypeName;
import io.electrica.connector.hub.repository.TokenAuthorizationRepository;
import io.electrica.connector.hub.rest.dto.CreateTokenAuthorizationDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TokenAuthorizationService {

    private final AuthorizationService authorizationService;
    private final TokenAuthorizationRepository tokenAuthorizationRepository;

    public TokenAuthorizationService(AuthorizationService authorizationService,
                                     TokenAuthorizationRepository tokenAuthorizationRepository) {
        this.authorizationService = authorizationService;
        this.tokenAuthorizationRepository = tokenAuthorizationRepository;
    }

    /**
     * Given the connection id,
     * it fetches or creates authorization if not exists.
     * <p>
     * For that authorization id,
     * it fetches the corresponding token authorization if exists
     * and sets the token if it has changed.
     * <p>
     * If token authorization does not exists, it creates a new one.
     */
    @Transactional
    public Authorization upsert(Long connectionId, CreateTokenAuthorizationDto dto) {

        final Authorization authorization =
                authorizationService.createIfAbsent(connectionId, AuthorizationTypeName.TOKEN_AUTHORIZATION, dto);

        final TokenAuthorization tokenAuthorization = tokenAuthorizationRepository
                .findOneByAuthorizationId(authorization.getId())
                .orElse(new TokenAuthorization());

        tokenAuthorization.setToken(dto.getToken());
        tokenAuthorization.setAuthorization(authorization);
        tokenAuthorizationRepository.save(tokenAuthorization);
        return authorization;
    }
}
