package io.electrica.user.repository;

import io.electrica.user.model.AccessKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the AccessKeyRepository entity.
 */
@Repository
public interface AccessKeyRepository extends JpaRepository<AccessKey, Long> {

    @Query(value = "from AccessKey ak where ak.archived = FALSE and ak.user.id = :userId order by ak.name ASC")
    List<AccessKey> findAllNonArchivedByUser(@Param("userId") Long userId);

    @Query(value =
            "SELECT EXISTS(" +
                    "SELECT 1 " +
                    "FROM access_keys ak " +
                    "WHERE ak.id=:accessKeyId and ak.user_id = :userId and ak.archived = FALSE)", nativeQuery = true)
    Boolean exists(@Param("accessKeyId") Long accessKeyId, @Param("userId") Long userId);

}
