package io.electrica.webhook.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class WebhookDto extends CreateWebhookDto {

    @NotNull
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String url;

    @NotNull
    private Long revisionVersion;

    private Long counter;

    private LocalDateTime createdAt;
}
