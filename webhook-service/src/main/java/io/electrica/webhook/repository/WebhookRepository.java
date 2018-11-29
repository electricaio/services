package io.electrica.webhook.repository;

import io.electrica.webhook.model.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface WebhookRepository extends JpaRepository<Webhook, UUID> {

    @Query(value = " from Webhook wh where wh.connectionId = :connectionId order by wh.name ASC")
    List<Webhook> findAllByConnection(@Param("connectionId") Long connectionId);

}
