package io.electrica.connector.hub.repository;

import io.electrica.connector.hub.model.Authorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthorizationRepository extends JpaRepository<Authorization, Long> {

    @Query("FROM Authorization a WHERE a.connection.id=:connectionId AND a.archived = FALSE")
    Optional<Authorization> findOneByConnectionId(@Param("connectionId") Long connectionId);
}
