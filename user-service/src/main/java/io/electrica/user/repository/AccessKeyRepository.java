package io.electrica.user.repository;

import io.electrica.user.model.AccessKey;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface AccessKeyRepository extends JpaRepository<AccessKey, Long> {

    @Query(value = "SELECT ak.* FROM access_keys ak WHERE ak.archived = FALSE and user_id = :userId",
            nativeQuery = true)
    List<AccessKey> findAllNonArchivedByUser(@Param("userId") Long userId);

    @Query(value = "SELECT ak.* FROM access_keys ak WHERE ak.archived = FALSE and ak.id = :accessKeyId" +
            " and ak.user_id = :userId ORDER BY key_name ASC",
            nativeQuery = true)
    AccessKey findByKeyAndUser(@Param("accessKeyId") Long accessKeyId, @Param("userId") Long userId);

}
