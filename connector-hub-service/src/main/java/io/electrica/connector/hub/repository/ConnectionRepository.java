package io.electrica.connector.hub.repository;

import io.electrica.connector.hub.model.Connection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {
}
