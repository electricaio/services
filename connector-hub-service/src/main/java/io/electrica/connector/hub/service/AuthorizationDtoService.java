package io.electrica.connector.hub.service;

import com.github.dozermapper.core.Mapper;
import io.electrica.common.context.IdentityContextHolder;
import io.electrica.connector.hub.rest.dto.ReadAuthorizationDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthorizationDtoService {

    private final IdentityContextHolder identityContextHolder;

    private final AuthorizationService authorizationService;

    private final Mapper mapper;

    public AuthorizationDtoService(IdentityContextHolder identityContextHolder,
                                   AuthorizationService authorizationService,
                                   Mapper mapper) {
        this.identityContextHolder = identityContextHolder;
        this.authorizationService = authorizationService;
        this.mapper = mapper;
    }

    public List<ReadAuthorizationDto> findAllByConnectionId(Long connectionId) {
        return authorizationService.findAllByConnectionId(connectionId)
                .stream()
                .map(t -> mapper.map(t, ReadAuthorizationDto.class))
                .collect(Collectors.toList());
    }
}
