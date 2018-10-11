package io.electrica.user.repository;

import io.electrica.user.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *  A Spring data repository for Organization Entity.
 *
 */
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}
