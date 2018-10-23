package io.electrica.connector.hub.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBasicAuthorizationDto {

    @NotBlank
    @Size(max = 255)
    private String user;

    @NotBlank
    @Size(max = 255)
    private String password;
}
