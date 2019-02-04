package io.electrica.metric.instance.session.repository;

import io.electrica.metric.instance.session.model.InstanceSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InstanceSessionRepository extends JpaRepository<InstanceSession, UUID>,
        JpaSpecificationExecutor<InstanceSession> {
}
