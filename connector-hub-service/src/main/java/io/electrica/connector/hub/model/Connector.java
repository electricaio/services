package io.electrica.connector.hub.model;

import io.electrica.common.jpa.model.AbstractEntity;
import io.electrica.connector.hub.dto.AuthorizationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@ToString
@NoArgsConstructor

@Audited
@Entity
@Table(name = "connectors",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "resource", "version"})
)
public class Connector extends AbstractEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_id", nullable = false)
    private ConnectorType type;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthorizationType authorizationType;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @Size(max = 255)
    @Column
    private String resource;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String version;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String namespace;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String ern;

    @Type(type = JSONB_TYPE)
    private Map<String, String> properties;

    public Optional<String> getResourceOpt() {
        return Optional.ofNullable(resource);
    }

}
