package io.electrica.user.repository;

import io.electrica.user.model.AccessKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface AccessKeyRepository extends JpaRepository<AccessKey, Long> {

    @Query(value = "from AccessKey ak where ak.archived = FALSE and ak.user.id = :userId order by ak.name ASC")
    List<AccessKey> findAllNonArchivedByUser(@Param("userId") Long userId);

}
