package io.electrica.stl.repository;

import io.electrica.stl.model.STLInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface STLInstanceRepository extends JpaRepository<STLInstance, Long> {
}
