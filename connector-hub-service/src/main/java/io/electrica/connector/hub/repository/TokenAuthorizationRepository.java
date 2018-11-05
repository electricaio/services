package io.electrica.connector.hub.repository;

import io.electrica.connector.hub.model.TokenAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenAuthorizationRepository extends JpaRepository<TokenAuthorization, Long> {
}
