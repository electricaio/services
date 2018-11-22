package io.electrica.webhook.model;

import io.electrica.common.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@ToString
@NoArgsConstructor

@Entity
@Table(name = "webhooks",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "connection_id"})
)
public class Webhook extends AbstractEntity {

    @NotNull
    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @Column(unique = true)
    private UUID token;

    @NotNull
    @Column(name = "connection_id")
    private Long connectionId;

    @NotNull
    @Column(name = "organization_id")
    private Long organizationId;

    @NotNull
    @Column(name = "user_id")
    private Long userId;


    @NotNull
    @Column(name = "connector_id")
    private Long connectorId;

    @NotNull
    @Column(name = "counter", nullable = false, columnDefinition = "int8 default 0")
    private Long counter;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @NotNull
    private String hash;

}
