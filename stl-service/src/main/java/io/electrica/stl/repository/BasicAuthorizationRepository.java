package io.electrica.stl.repository;

import io.electrica.stl.model.BasicAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasicAuthorizationRepository extends JpaRepository<BasicAuthorization, Long> {
}
