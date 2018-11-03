package io.electrica.user.service.dto;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.dto.AbstractDtoService;
import io.electrica.user.dto.CreateOrganizationDto;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.model.Organization;
import io.electrica.user.service.OrganizationService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Organization Dto Service implementation class for managing organization.
 */
@Component
public class OrganizationDtoService extends AbstractDtoService<Organization, CreateOrganizationDto, OrganizationDto> {

    private final OrganizationService organizationService;

    @Inject
    public OrganizationDtoService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @Override
    protected AbstractService<Organization> getService() {
        return organizationService;
    }

    public OrganizationDto createIfAbsent(CreateOrganizationDto dto) {
        return toDto(organizationService.findByName(dto.getName())
                .orElseGet(() -> organizationService.create(toCreateEntity(dto)))
        );
    }

    @Override
    protected Class<Organization> getEntityClass() {
        return Organization.class;
    }

    @Override
    protected Class<OrganizationDto> getDtoClass() {
        return OrganizationDto.class;
    }

}
