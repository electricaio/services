package io.electrica.connector.hub.repository;

import io.electrica.connector.hub.model.BasicAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasicAuthorizationRepository extends JpaRepository<BasicAuthorization, Long> {
}
