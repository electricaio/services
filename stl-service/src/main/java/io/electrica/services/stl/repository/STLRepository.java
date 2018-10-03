package io.electrica.services.stl.repository;

import io.electrica.services.stl.model.STL;
import org.springframework.data.jpa.repository.JpaRepository;

public interface STLRepository extends JpaRepository<STL, Long> {
}
