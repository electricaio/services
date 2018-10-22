package io.electrica.connector.hub.repository;

import io.electrica.connector.hub.model.AuthorizationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorizationTypeRepository extends JpaRepository<AuthorizationType, Long> {
}
