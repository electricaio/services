package io.electrica.stl.repository;

import io.electrica.stl.model.TokenAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenAuthorizationRepository extends JpaRepository<TokenAuthorization, Long> {

    @Query(value = "SELECT ta.* " +
            "FROM token_authorizations ta " +
            "INNER JOIN authorizations a ON ta.authorization_id=a.id " +
            "WHERE a.stl_instance_id=:stlInstanceId", nativeQuery = true)
    Optional<TokenAuthorization> findBySTLInstance(@Param("stlInstanceId") Long stlInstanceId);
}
