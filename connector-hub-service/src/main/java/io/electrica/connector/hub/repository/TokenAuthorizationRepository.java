package io.electrica.connector.hub.repository;

import io.electrica.connector.hub.model.TokenAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenAuthorizationRepository extends JpaRepository<TokenAuthorization, Long> {

    @Query("FROM TokenAuthorization ba WHERE ba.authorization.id=:authorizationId AND ba.archived=FALSE")
    Optional<TokenAuthorization> findOneByAuthorizationId(@Param("authorizationId") Long authorizationId);

}
