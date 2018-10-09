package io.electrica.stl.repository;

import io.electrica.stl.model.Authorization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorizationRepository extends JpaRepository<Authorization, Long> {
}
