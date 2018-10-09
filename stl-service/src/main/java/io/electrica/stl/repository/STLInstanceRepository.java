package io.electrica.stl.repository;

import io.electrica.stl.model.STLInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface STLInstanceRepository extends JpaRepository<STLInstance, Long> {

    @Query(value =
            "SELECT EXISTS(" +
                    "SELECT TRUE " +
                    "FROM stl_instances si " +
                    "WHERE si.stl_id=?1 AND si.access_key_id=?2)", nativeQuery = true)
    Boolean exists(Long stlId, Long accessKeyId);
}
