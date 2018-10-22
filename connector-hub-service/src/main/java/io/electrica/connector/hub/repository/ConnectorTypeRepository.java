package io.electrica.connector.hub.repository;

import io.electrica.connector.hub.model.ConnectorType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectorTypeRepository extends JpaRepository<ConnectorType, Long> {
}
