package io.electrica.webhook.model;

import io.electrica.common.jpa.hibernate.JsonObjectUserType;
import io.electrica.webhook.dto.WebhookScope;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;


@Getter
@Setter
@ToString
@NoArgsConstructor

@Entity
@Table(
        name = "webhooks",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "accessKeyId"}),
        indexes = {
                @Index(name = "webhooks_connector_id_idx", columnList = "connectorId"),
                @Index(name = "webhooks_connection_id_idx", columnList = "connectionId")
        }
)
@TypeDef(name = Webhook.JSONB_TYPE, typeClass = JsonObjectUserType.class)
public class Webhook {

    public static final String JSONB_TYPE = "jsonb";

    @Id
    private UUID id;

    @NotNull
    @Size(max = 128)
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private Long organizationId;

    @NotNull
    @Column(nullable = false)
    private Long userId;

    @NotNull
    @Column(nullable = false)
    private Long accessKeyId;

    @NotNull
    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean isPublic;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 31, updatable = false)
    private WebhookScope scope;

    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Additional data for {@link WebhookScope#Connector} scope.
     */
    @Column
    private Long connectorId;

    /**
     * Additional data for {@link WebhookScope#Connector} scope.
     */
    @Column
    private String connectorErn;

    /**
     * Additional data for {@link WebhookScope#Connection} scope.
     */
    @Column
    private Long connectionId;

    @Type(type = JSONB_TYPE)
    private Map<String, String> properties;

}
