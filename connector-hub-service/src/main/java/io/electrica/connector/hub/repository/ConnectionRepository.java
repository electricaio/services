package io.electrica.connector.hub.repository;

import io.electrica.connector.hub.model.Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    @Query(value =
            "SELECT EXISTS(" +
            "SELECT TRUE " +
            "FROM connections c " +
            "WHERE c.id=:connectionId AND c.user_id=:userId AND c.archived=FALSE)", nativeQuery = true)
    Boolean exists(@Param("connectionId") Long connectionId, @Param("userId") Long userId);

    @Query("FROM Connection c WHERE c.userId=:userId AND c.archived=FALSE")
    List<Connection> findAllByUser(@Param("userId") Long userId);
}
