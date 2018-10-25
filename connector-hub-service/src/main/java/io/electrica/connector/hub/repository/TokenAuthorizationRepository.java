package io.electrica.connector.hub.repository;

import io.electrica.connector.hub.model.TokenAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenAuthorizationRepository extends JpaRepository<TokenAuthorization, Long> {

    @Query("FROM TokenAuthorization ta WHERE ta.authorization.id=:authorizationId AND ta.archived=FALSE")
    Optional<TokenAuthorization> findOneByAuthorizationId(@Param("authorizationId") Long authorizationId);

}
