package io.electrica.stl.repository;

import io.electrica.stl.model.Connector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConnectorRepository extends JpaRepository<Connector, Long> {

    @Query(value = "FROM Connector c WHERE c.archived = FALSE")
    List<Connector> findAllNonArchived();
}
