package io.electrica.connector.hub.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CreateTokenAuthorizationDto extends CreateAuthorizationDto {

    @NotBlank
    @Size(max = 255)
    private String token;
}
