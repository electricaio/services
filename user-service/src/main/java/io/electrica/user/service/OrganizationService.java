package io.electrica.user.service;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.user.model.Organization;
import io.electrica.user.repository.OrganizationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Organization Service implementation class for managing roles.
 */
@Component
public class OrganizationService extends AbstractService<Organization> {

    private OrganizationRepository organizationRepository;

    @Inject
    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public Optional<Organization> findByName(String name) {
        return organizationRepository.findOneByNameIgnoreCase(name);
    }

    @Override
    protected Organization executeCreate(Organization newEntity) {
        return organizationRepository.save(newEntity);
    }

    @Override
    protected void executeUpdate(Organization merged, Organization update) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected JpaRepository<Organization, Long> getRepository() {
        return organizationRepository;
    }

}
