package io.electrica.user.repository;

import io.electrica.user.model.RoleToPermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleToPermissionRepository extends JpaRepository<RoleToPermission, Long> {
}
