package io.electrica.user.service;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.validation.EntityValidator;
import io.electrica.user.model.Organization;
import io.electrica.user.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;

/**
 * Organization Service implementation class for managing roles.
 */
@Component
public class OrganizationService extends AbstractService<Organization> {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private OrganizationRepository organizationRepository;

    @Inject
    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    protected Collection<String> getContainerValidators() {
        return Collections.EMPTY_LIST;
    }

    @Override
    protected Collection<EntityValidator<Organization>> getValidators() {
        return Collections.EMPTY_LIST;
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
}
