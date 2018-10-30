package io.electrica.connector.hub.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTokenAuthorizationDto extends AuthorizationDto {

    @NotBlank
    @Max(255)
    private String token;
}
