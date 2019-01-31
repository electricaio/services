package io.electrica.instance.session.repository;

import io.electrica.instance.session.model.InstanceSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InstanceSessionRepository extends JpaRepository<InstanceSession, UUID> {

    Page<InstanceSession> findByAccessKeyId(long accessKeyId, Pageable pageable);

    Page<InstanceSession> findByUserId(long userId, Pageable pageable);

    Page<InstanceSession> findByOrganizationId(long organizationId, Pageable pageable);
}
