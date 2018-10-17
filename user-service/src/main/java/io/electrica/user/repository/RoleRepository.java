package io.electrica.user.repository;

import io.electrica.common.security.RoleType;
import io.electrica.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * A Spring data repository for Role Entity.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findOneByType(RoleType type);
}
