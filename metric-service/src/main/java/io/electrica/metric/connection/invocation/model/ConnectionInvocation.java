package io.electrica.metric.connection.invocation.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "connection_invocations",
        indexes = {
                @Index(name = "connection_invocations_instance_id_idx", columnList = "instanceId"),
                @Index(name = "connection_invocations_user_id_idx", columnList = "userId"),
                @Index(name = "connection_invocations_access_key_id_idx", columnList = "accessKeyId"),
                @Index(name = "connection_invocations_connection_id_idx", columnList = "connectionId"),
                @Index(name = "connection_invocations_connector_id_idx", columnList = "connectorId"),
        }
)
public class ConnectionInvocation {
    @Id
    private UUID invocationId;

    @NotNull
    @Column(nullable = false)
    private UUID instanceId;

    @NotNull
    @Column(nullable = false)
    private Long userId;

    @NotNull
    @Column(nullable = false)
    private Long organizationId;

    @NotNull
    @Column(nullable = false)
    private Long accessKeyId;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @NotNull
    @Column(nullable = false)
    private Long connectionId;

    @NotNull
    @Column(nullable = false)
    private Long connectionRevisionVersion;

    @Column
    private Long authorizationId;

    @NotNull
    @Column(nullable = false)
    private Long connectorId;

    @NotNull
    @Column(nullable = false)
    private Long connectorRevisionVersion;

    @NotBlank
    @Column(nullable = false)
    private String ern;

    @NotNull
    @Column(nullable = false)
    private ConnectionInvocationStatus status;

    @Column
    @Size(max = 255)
    private String action;

    @Column(columnDefinition = "TEXT")
    @Type(type = "io.electrica.common.jpa.hibernate.JsonNodeToStringUserType")
    private JsonNode parameters;

    @Column(columnDefinition = "TEXT")
    @Type(type = "io.electrica.common.jpa.hibernate.JsonNodeToStringUserType")
    private JsonNode payload;

    @Column(columnDefinition = "TEXT")
    @Type(type = "io.electrica.common.jpa.hibernate.JsonNodeToStringUserType")
    private JsonNode result;

    @Column
    private String errorCode;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(columnDefinition = "TEXT")
    private String stackTrace;

    @Column(columnDefinition = "TEXT")
    @Type(type = "io.electrica.common.jpa.hibernate.ListToJsonStringUserType")
    private List<String> errorPayload;

    @Version
    @Column(nullable = false)
    private Long revisionVersion;
}
