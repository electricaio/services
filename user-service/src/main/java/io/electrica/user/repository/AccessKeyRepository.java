package io.electrica.user.repository;

import io.electrica.user.model.AccessKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface AccessKeyRepository extends JpaRepository<AccessKey, Long> {

}
