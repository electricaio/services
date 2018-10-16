package io.electrica.user.repository;

import io.electrica.user.model.AccessKey;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface AccessKeyRepository extends JpaRepository<AccessKey, Long> {

    @Query(value = "from AccessKey ak where ak.archived = FALSE and user_id = :userId order by key_name ASC")
    List<AccessKey> findAllNonArchivedByUser(@Param("userId") Long userId);

    @Query(value = "from AccessKey ak where ak.archived = FALSE and ak.id = :accessKeyId and ak.user.id = :userId")
    Optional<AccessKey> findByKeyAndUser(@Param("accessKeyId") Long accessKeyId, @Param("userId") Long userId);

}
