package io.electrica.metric.webhook.invocation.model;

import io.electrica.webhook.dto.WebhookScope;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "webhook_invocations",
        indexes = {
                @Index(name = "webhook_invocations_webhook_id_idx", columnList = "webhookId"),
                @Index(name = "webhook_invocations_webhook_service_id_idx", columnList = "webhookServiceId"),
                @Index(name = "webhook_invocations_user_id_idx", columnList = "userId"),
                @Index(name = "webhook_invocations_access_key_id_idx", columnList = "accessKeyId")
        }
)
public class WebhookInvocation {

    @Id
    private UUID messageId;

    @Column
    private UUID webhookId;

    @Column
    private UUID webhookServiceId;

    @Column
    private String webhookName;

    @Column
    private Long organizationId;

    @Column
    private Long userId;

    @Column
    private Long accessKeyId;

    @Column
    private Boolean isPublic;

    @Column
    private WebhookScope scope;

    @Column
    private Long connectorId;

    @Column
    private String connectorErn;

    @Column
    private Long connectionId;

    @Column
    @Type(type = "io.electrica.common.jpa.hibernate.JsonObjectUserType")
    private Map<String, String> properties;

    @Column
    private Boolean expectedResult;

    @Column
    private String expectedContentType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column
    private String contentType;

    @Column
    private UUID sdkInstanceId;

    @Column
    private String resultPayload;

    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @NotNull
    @Column(length = 31, nullable = false)
    @Enumerated(EnumType.STRING)
    private WebhookInvocationStatus status;

    @Column
    private String errorMessage;

    @Column
    private LocalDateTime errorTime;

    @Version
    @Column(nullable = false)
    private Long revisionVersion;
}

