package io.electrica.webhook.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ConnectionWebhookDto extends ConnectionCreateWebhookDto {

    private UUID id;
    private Long organizationId;
    private Long userId;
    private String url;
    private LocalDateTime createdAt;

}
