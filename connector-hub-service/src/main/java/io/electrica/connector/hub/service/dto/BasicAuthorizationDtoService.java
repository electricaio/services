package io.electrica.connector.hub.service.dto;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.dto.AbstractDtoService;
import io.electrica.connector.hub.dto.BasicAuthorizationDto;
import io.electrica.connector.hub.dto.CreateBasicAuthorizationDto;
import io.electrica.connector.hub.model.BasicAuthorization;
import io.electrica.connector.hub.service.BasicAuthorizationService;
import org.springframework.stereotype.Component;

/**
 * User Dto Service implementation class for basic authorization.
 */
@Component
public class BasicAuthorizationDtoService extends
        AbstractDtoService<BasicAuthorization, CreateBasicAuthorizationDto, BasicAuthorizationDto> {

    private final BasicAuthorizationService basicAuthorizationService;

    public BasicAuthorizationDtoService(BasicAuthorizationService basicAuthorizationService) {
        this.basicAuthorizationService = basicAuthorizationService;
    }

    public BasicAuthorizationDto createAndAssignToConnection(Long connectionId, CreateBasicAuthorizationDto dto) {
        return toDto(basicAuthorizationService.createAndAssignToConnection(connectionId, toCreateEntity(dto)));
    }

    @Override
    protected AbstractService<BasicAuthorization> getService() {
        return basicAuthorizationService;
    }

    @Override
    protected Class<BasicAuthorization> getEntityClass() {
        return BasicAuthorization.class;
    }

    @Override
    protected Class<BasicAuthorizationDto> getDtoClass() {
        return BasicAuthorizationDto.class;
    }
}
