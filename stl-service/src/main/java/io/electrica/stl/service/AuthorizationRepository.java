package io.electrica.stl.service;

import io.electrica.stl.model.Authorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthorizationRepository extends JpaRepository<Authorization, Long> {

    @Query(value = "FROM Authorization a WHERE a.stlInstance.id=:stlInstanceId")
    Optional<Authorization> findOneByStlInstance(@Param("stlInstanceId") Long stlInstanceId);
}
