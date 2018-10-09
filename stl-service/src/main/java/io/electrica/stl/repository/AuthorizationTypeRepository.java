package io.electrica.stl.repository;

import io.electrica.stl.model.AuthorizationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorizationTypeRepository extends JpaRepository<AuthorizationType, Long> {
}
