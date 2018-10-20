package io.electrica.stl.repository;

import io.electrica.stl.model.TokenAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenAuthorizationRepository extends JpaRepository<TokenAuthorization, Long> {

    @Query(value = "FROM TokenAuthorization ta WHERE ta.authorization.id=:authorizationId")
    Optional<TokenAuthorization> findOneByAuthorization(@Param("authorizationId") Long authorizationId);
}
