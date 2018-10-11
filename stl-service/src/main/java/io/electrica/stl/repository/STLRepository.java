package io.electrica.stl.repository;

import io.electrica.stl.model.STL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface STLRepository extends JpaRepository<STL, Long> {

    @Query(value = "SELECT s.* FROM stls s WHERE s.archived = FALSE", nativeQuery = true)
    List<STL> findAllNonArchived();
}
