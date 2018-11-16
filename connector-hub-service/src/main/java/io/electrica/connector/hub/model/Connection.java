package io.electrica.connector.hub.model;

import io.electrica.common.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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

    @Size(max = 255)
    @Column(name = "tenant_ref_id")
    private String tenantRefId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "connector_id", nullable = false)
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

    @OneToOne
    @JoinColumn(name = "authorization_id", unique = true)
    private Authorization authorization;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Map<String, String> properties;

}
