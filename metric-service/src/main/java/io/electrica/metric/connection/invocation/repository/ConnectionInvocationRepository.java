package io.electrica.metric.connection.invocation.repository;

import io.electrica.metric.connection.invocation.model.ConnectionInvocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ConnectionInvocationRepository extends JpaRepository<ConnectionInvocation, UUID>,
        JpaSpecificationExecutor<ConnectionInvocation> {
}
