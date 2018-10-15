package io.electrica.user.repository;

import io.electrica.user.model.UserToRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserToRoleRepository extends JpaRepository<UserToRole, Long> {
}
