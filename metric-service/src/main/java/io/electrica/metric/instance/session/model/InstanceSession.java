package io.electrica.metric.instance.session.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "instance_sessions",
        indexes = {
                @Index(name = "instance_sessions_user_id_idx", columnList = "userId"),
                @Index(name = "instance_sessions_organization_id_idx", columnList = "organizationId"),
                @Index(name = "instance_sessions_access_key_id_idx", columnList = "accessKeyId")
        }
)
public class InstanceSession {
    @Id
    @Column(nullable = false, unique = true)
    private UUID id;

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
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 31, nullable = false)
    private SessionState sessionState;

    /**
     * Client time of first session started. Setted once when Instance session created.
     * Client offset can be calculated as difference between clientTime and startedTime
     */
    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime startedClientTime;

    /**
     * Last session started client time. Updated each time Electrica instance reconnects.
     *
     * Consider the following case when client connected and immediately reconnect.
     * If client retry policy delay longer then web socket session timeout then there will be normal order of event:
     *  socketHandshake1 -> socketOnClose1 -> socketHandshake2
     * so instance sessionState will be
     *  running -> expired -> running
     * But due to network issues or small delay in retry policy order of events can be different:
     *  socketHandshake2 -> socketHandshake1 -> socketOnClose1
     * To avoid stopping of still alive instances or overriding more relevant InstanceSession
     * events with older lastSessionStarted will be ignored
     * so in such case instance sessionState will be
     *  running -> running -> running
     */
    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime lastSessionStarted;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime startedTime;

    @Column
    private LocalDateTime stoppedTime;

    @Column
    private LocalDateTime expiredTime;

    @Version
    @Column(nullable = false)
    private Long revisionVersion;
}
