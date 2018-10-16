package io.electrica.user.service;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.dto.AbstractDtoService;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.model.Organization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Organization Dto Service implementation class for managing organization.
 */
@Component
public class OrganizationDtoService extends AbstractDtoService<Organization, OrganizationDto> {

    private static final Logger log = LoggerFactory.getLogger(UserDtoService.class);

    private final OrganizationService organizationService;

    private final UserDtoService userDtoService;

    @Inject
    public OrganizationDtoService(OrganizationService organizationService, UserDtoService userDtoService) {
        this.organizationService = organizationService;
        this.userDtoService = userDtoService;
    }

    @Override
    protected AbstractService<Organization> getService() {
        return organizationService;
    }

    public OrganizationDto createIfAbsent(String orgName) {
        Optional<Organization> organization = organizationService.findByName(orgName);
        if (organization.isPresent()) {
            return toDto(organization.get());
        } else {
            return toDto(organizationService.create(toEntity(createOrgDtoFromName(orgName))));
        }
    }

    @Override
    protected Class<Organization> getEntityClass() {
        return Organization.class;
    }

    @Override
    protected Class<OrganizationDto> getDtoClass() {
        return OrganizationDto.class;
    }

    public List<UserDto> getUsersForOrganization(Long orgId) {
        return userDtoService.getUsersForOrg(orgId);
    }

    private OrganizationDto createOrgDtoFromName(String domain) {
        OrganizationDto org = new OrganizationDto();
        org.setName(domain);
        org.setUuid(UUID.randomUUID());
        return org;
    }
}
