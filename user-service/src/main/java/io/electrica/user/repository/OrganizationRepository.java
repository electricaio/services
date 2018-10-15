package io.electrica.user.repository;

import io.electrica.user.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 *  A Spring data repository for Organization Entity.
 *
 */
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findOneByNameIgnoreCase(String name);
}
