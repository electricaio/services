package io.electrica.user.repository;

import io.electrica.user.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "userToRoles.role.roleToPermissions.permission")
    Optional<User> findOneByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "userToRoles.role.roleToPermissions.permission")
    Optional<User> findOneById(Long id);

}
