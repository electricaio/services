package io.electrica.stl.repository;

import io.electrica.stl.model.STL;
import org.springframework.data.jpa.repository.JpaRepository;

public interface STLRepository extends JpaRepository<STL, Long> {
}
