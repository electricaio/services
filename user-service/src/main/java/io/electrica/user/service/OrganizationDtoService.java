package io.electrica.user.service;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.dto.AbstractDtoService;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.model.Organization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Organization Dto Service implementation class for managing organization.
 */
@Component
public class OrganizationDtoService extends AbstractDtoService<Organization, OrganizationDto> {

    private static final Logger log = LoggerFactory.getLogger(UserDtoService.class);

    private final OrganizationService organizationService;

    @Inject
    public OrganizationDtoService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @Override
    protected AbstractService<Organization> getService() {
        return organizationService;
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
