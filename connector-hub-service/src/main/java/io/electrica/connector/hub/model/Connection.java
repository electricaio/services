package io.electrica.connector.hub.model;

import io.electrica.common.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter

@Entity
@Audited
@Table(
        name = "connections",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "access_key_id"}),
        indexes = {@Index(name = "connections_user_id_idx", columnList = "user_id"),
                @Index(name = "connections_access_key_id_idx", columnList = "access_key_id")
        }
)
public class Connection extends AbstractEntity {

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "connector_id", nullable = false, updatable = false)
    private Connector connector;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @NotNull
    @Column(name = "access_key_id", nullable = false)
    private Long accessKeyId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "authorization_id", unique = true)
    private Authorization authorization;

    @Type(type = JSONB_TYPE)
    private Map<String, String> properties;

    @Column
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
