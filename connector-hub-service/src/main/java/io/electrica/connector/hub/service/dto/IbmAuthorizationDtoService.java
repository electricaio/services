package io.electrica.connector.hub.service.dto;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.dto.AbstractDtoService;
import io.electrica.connector.hub.dto.CreateIbmAuthorizationDto;
import io.electrica.connector.hub.dto.IbmAuthorizationDto;
import io.electrica.connector.hub.model.IbmAuthorization;
import io.electrica.connector.hub.service.IbmAuthorizationService;
import org.springframework.stereotype.Component;

@Component
public class IbmAuthorizationDtoService extends
        AbstractDtoService<IbmAuthorization, CreateIbmAuthorizationDto, IbmAuthorizationDto> {

    private final IbmAuthorizationService ibmAuthorizationService;

    public IbmAuthorizationDtoService(IbmAuthorizationService ibmAuthorizationService) {
        this.ibmAuthorizationService = ibmAuthorizationService;
    }

    public IbmAuthorizationDto createAndAssignToConnection(Long connectionId, CreateIbmAuthorizationDto dto) {
        return toDto(ibmAuthorizationService.createAndAssignToConnection(connectionId, toCreateEntity(dto)));
    }

    @Override
    protected AbstractService<IbmAuthorization> getService() {
        return ibmAuthorizationService;
    }

    @Override
    protected Class<IbmAuthorization> getEntityClass() {
        return IbmAuthorization.class;
    }

    @Override
    protected Class<IbmAuthorizationDto> getDtoClass() {
        return IbmAuthorizationDto.class;
    }
}
