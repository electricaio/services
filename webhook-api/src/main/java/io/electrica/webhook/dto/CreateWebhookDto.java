package io.electrica.webhook.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CreateWebhookDto {

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotNull
    private Long connectionId;

    @NotNull
    private Long organizationId;

    @NotNull
    private Long userId;

    @NotNull
    private Long connectorId;

}
