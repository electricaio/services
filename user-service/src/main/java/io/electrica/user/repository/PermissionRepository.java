package io.electrica.user.repository;

import io.electrica.user.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 *  A Spring data repository for Permission Entity.
 *
 */
public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
