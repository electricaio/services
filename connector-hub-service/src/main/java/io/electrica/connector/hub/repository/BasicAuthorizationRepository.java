package io.electrica.connector.hub.repository;

import io.electrica.connector.hub.model.BasicAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BasicAuthorizationRepository extends JpaRepository<BasicAuthorization, Long> {

    @Query("FROM BasicAuthorization ba WHERE ba.authorization.id=:authorizationId AND ba.archived=FALSE")
    Optional<BasicAuthorization> findOneByAuthorizationId(@Param("authorizationId") Long authorizationId);
}
