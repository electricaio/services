package io.electrica.webhook.message;

import com.fasterxml.jackson.databind.JsonNode;
import io.electrica.webhook.dto.WebhookScope;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class WebhookMessage {

    @NotNull
    private UUID id;

    @NotNull
    private String name;

    @NotNull
    private Long organizationId;

    @NotNull
    private Long userId;

    @NotNull
    private Long accessKeyId;

    @NotNull
    private WebhookScope scope;

    /**
     * Additional data for {@link WebhookScope#Connector} scope.
     */
    private Long connectorId;

    /**
     * Additional data for {@link WebhookScope#Connection} scope.
     */
    private Long connectionId;

    private Map<String, String> properties;

    @NotNull
    private Boolean expectedResult;

    @NotNull
    private JsonNode payload;

}
