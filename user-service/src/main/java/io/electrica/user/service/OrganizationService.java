package io.electrica.user.service;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.validation.ContainerEntityValidator;
import io.electrica.common.jpa.service.validation.EntityValidator;
import io.electrica.user.model.Organization;
import io.electrica.user.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;

/**
 * Organization Service implementation class for managing roles.
 */
@Component
public class OrganizationService extends AbstractService<Organization> {

    private final Logger log = LoggerFactory.getLogger(OrganizationService.class);

    private OrganizationRepository organizationRepository;

    @Inject
    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    protected Collection<String> getContainerValidators() {
        return Arrays.asList(
                ContainerEntityValidator.TRIMMED_STRINGS,
                ContainerEntityValidator.AVOID_EMTPY_STRINGS
        );
    }

    public Organization createIfAbsent(final String email) {
        String domain = email.substring(email.indexOf("@") + 1);
        Optional<Organization> org = organizationRepository.findOneByNameIgnoreCase(domain);
        if (org.isPresent()) {
            return org.get();
        } else {
            return createOrgFromDomain(domain);
        }
    }

    @Override
    protected Collection<EntityValidator<Organization>> getValidators() {
        return Collections.emptyList();
    }

    @Override
    protected Organization executeCreate(Organization newEntity) {
        return organizationRepository.save(newEntity);
    }

    @Override
    protected void executeUpdate(Organization merged, Organization update) {

    }

    @Override
    protected JpaRepository<Organization, Long> getRepository() {
        return organizationRepository;
    }

    public Organization createOrgFromDomain(String domain) {
        Organization org = new Organization();
        org.setName(domain);
        org.setUuid(UUID.randomUUID());
        return organizationRepository.save(org);
    }
}
