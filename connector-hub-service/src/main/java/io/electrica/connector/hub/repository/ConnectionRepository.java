package io.electrica.connector.hub.repository;

import io.electrica.connector.hub.model.Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    @Query(
            value = "" +
                    " SELECT EXISTS (" +
                    "   SELECT TRUE" +
                    "   FROM connections c" +
                    "   WHERE c.id=:connectionId AND c.user_id=:userId AND c.archived=FALSE" +
                    ")",
            nativeQuery = true
    )
    Boolean canUserAccessConnection(@Param("connectionId") Long connectionId, @Param("userId") Long userId);

    @Query(
            value = "" +
                    " SELECT EXISTS (" +
                    "   SELECT TRUE" +
                    "   FROM connections c" +
                    "   WHERE c.authorization_id = :authorizationId AND c.user_id = :userId AND c.archived=FALSE" +
                    " )",
            nativeQuery = true
    )
    Boolean canUserAccessAuthorization(@Param("authorizationId") Long authorizationId, @Param("userId") Long userId);

    @Query("" +
            " from Connection c" +
            "   left join fetch c.connector cn" +
            "   left join fetch c.authorization a" +
            " where c.id = :connectionId and c.archived is false"
    )
    Optional<Connection> findByIdWithConnectorAndAuthorization(@Param("connectionId") Long connectionId);

    @Query("" +
            " from Connection c " +
            " left join fetch c.connector cn" +
            " where c.accessKeyId = :accessKeyId and c.archived is false and " +
            "(:connectionName is null or c.name = :connectionName)  and " +
            "(:ern is null or cn.ern = :ern )   " +
            " order by c.name "
    )
    List<Connection> findByAccessKeyWithFilter(@Param("accessKeyId") Long accessKeyId,
                                               @Nullable @Param("connectionName") String connectionName,
                                               @Nullable @Param("ern") String ern);


    @Query("FROM Connection c WHERE c.userId=:userId AND c.archived=FALSE order by c.name")
    List<Connection> findAllByUser(@Param("userId") Long userId);
}
