package io.electrica.connector.hub.repository;

import io.electrica.connector.hub.model.Authorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthorizationRepository extends JpaRepository<Authorization, Long> {

    @Query("FROM Authorization a " +
            "WHERE a.name=:name " +
            "AND a.connection.id=:connectionId " +
            "AND a.archived = FALSE")
    Optional<Authorization> findOneByNameAndConnectionId(
            @Param("name") String name,
            @Param("connectionId") Long connectionId);

    @Query("SELECT a " +
            "FROM Authorization a " +
            "WHERE a.connection.id=:connectionId " +
            "AND a.archived = FALSE")
    List<Authorization> findAllByConnectionId(@Param("connectionId") Long connectionId);
}
