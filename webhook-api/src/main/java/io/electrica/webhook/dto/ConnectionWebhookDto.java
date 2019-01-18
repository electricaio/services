package io.electrica.webhook.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ConnectionWebhookDto extends ConnectionCreateWebhookDto implements WebhookDto {

    private UUID id;
    private Long organizationId;
    private Long userId;
    private LocalDateTime createdAt;

    private String submitUrl;
    private String invokeUrl;
    private String publicSubmitUrl;
    private String publicInvokeUrl;

}
