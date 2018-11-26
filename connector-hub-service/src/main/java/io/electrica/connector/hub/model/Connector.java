package io.electrica.connector.hub.model;

import io.electrica.common.jpa.model.AbstractEntity;
import io.electrica.connector.hub.dto.AuthorizationType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

@Getter
@Setter
@ToString

@Audited
@Entity
@Table(name = "connectors")
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

    @NotNull
    @Size(max = 127)
    @Column(nullable = false, length = 127)
    private String namespace;

    @Size(max = 63)
    @Column(length = 63)
    private String resource;

    @NotNull
    @Size(max = 63)
    @Column(nullable = false, length = 63)
    private String version;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String ern;

    @Type(type = JSONB_TYPE)
    private Map<String, String> properties;

    @Size(max = 255)
    private String sourceURL;

    @Size(max = 255)
    private String connectorURL;

    @Size(max = 255)
    private String sdkURL;

    @Size(max = 255)
    private String imageURL;

    @Size(max = 255)
    private String description;

}
