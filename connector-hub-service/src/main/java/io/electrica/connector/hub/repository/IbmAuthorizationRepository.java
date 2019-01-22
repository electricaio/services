package io.electrica.connector.hub.repository;

import io.electrica.connector.hub.model.IbmAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IbmAuthorizationRepository extends JpaRepository<IbmAuthorization, Long> {
}
