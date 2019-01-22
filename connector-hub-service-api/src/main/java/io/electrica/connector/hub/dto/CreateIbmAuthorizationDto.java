package io.electrica.connector.hub.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CreateIbmAuthorizationDto {

    @NotBlank
    @Size(max = 31)
    private String integrationId;

    @NotBlank
    @Size(max = 31)
    private String clientId;
}
