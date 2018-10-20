package io.electrica.stl.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class AuthorizationDto {

    @Size(max = 255)
    private String user;

    @Size(max = 255)
    private String password;

    @Size(max = 255)
    private String token;

    @Size(max = 255)
    private String details;
}
