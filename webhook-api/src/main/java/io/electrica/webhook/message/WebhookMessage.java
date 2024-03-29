package io.electrica.webhook.message;

import io.electrica.webhook.dto.WebhookScope;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WebhookMessage {

    @NotNull
    private UUID id;

    @NotNull
    private UUID webhookId;

    @NotNull
    private UUID webhookServiceId;

    @NotNull
    private String name;

    @NotNull
    private Long organizationId;

    @NotNull
    private Long userId;

    @NotNull
    private Long accessKeyId;

    @NotNull
    private Boolean isPublic;

    @NotNull
    private WebhookScope scope;

    /**
     * Additional data for {@link WebhookScope#Connector} scope.
     */
    private Long connectorId;

    /**
     * Additional data for {@link WebhookScope#Connector} scope.
     */
    private String connectorErn;

    /**
     * Additional data for {@link WebhookScope#Connection} scope.
     */
    private Long connectionId;

    private Map<String, String> properties;

    @NotNull
    private Boolean expectedResult;

    private String expectedContentType;

    @NotNull
    private String payload;

    @NotNull
    private String contentType;

}
