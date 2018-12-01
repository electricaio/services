package io.electrica.webhook.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ConnectionCreateWebhookDto extends CreateWebhookDto {

    @NotNull
    private Long connectionId;

}
