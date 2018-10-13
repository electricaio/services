package io.electrica.user;

import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.model.Organization;

import java.util.Date;
import java.util.UUID;

/**
 * Base Class For Unit Tests.
 */
public abstract class TestBase {

    protected OrganizationDto createNewOrgDto() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test" + new Date().getTime());
        organizationDto.setIsActive(Boolean.TRUE);
        organizationDto.setUuid(UUID.randomUUID());
        return organizationDto;
    }

    protected Organization toEntity(OrganizationDto organizationDto) {
        Organization organization = new Organization();
        organization.setName(organizationDto.getName());
        organization.setUuid(organizationDto.getUuid());
        organization.setIsActive(organizationDto.getIsActive());
        return organization;
    }

    protected Organization createNewOrg() {
        Organization organization = new Organization();
        organization.setName("test" + new Date().getTime());
        organization.setIsActive(Boolean.TRUE);
        organization.setUuid(UUID.randomUUID());
        return organization;
    }

    protected OrganizationDto cloneOrgDtoAndAddRevisionNumber(OrganizationDto input) {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName(input.getName());
        organizationDto.setIsActive(input.getIsActive());
        organizationDto.setUuid(input.getUuid());
        organizationDto.setRevisionVersion(1L);
        return organizationDto;
    }

}