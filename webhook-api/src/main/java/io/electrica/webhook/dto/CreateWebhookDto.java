package io.electrica.webhook.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

@Getter
@Setter
public abstract class CreateWebhookDto {

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotNull
    private Long accessKeyId;

    private Map<String, String> properties;

}
