package io.electrica.webhook.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class WebhookDto extends CreateWebhookDto {

    @NotNull
    private UUID id;

    @NotBlank
    @Size(max = 255)
    private String url;

    private Long invocationsCount;

    private LocalDateTime createdAt;
}
