package io.electrica.connector.hub.repository;

import io.electrica.connector.hub.model.AuthorizationType;
import io.electrica.connector.hub.model.enums.AuthorizationTypeName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorizationTypeRepository extends JpaRepository<AuthorizationType, Long> {

    Optional<AuthorizationType> findOneByName(AuthorizationTypeName name);
}
