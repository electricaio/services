package io.electrica.stl.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class AuthorizationData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 255)
    private String user;

    @Size(max = 255)
    private String password;

    @Size(max = 255)
    private String token;

    @Size(max = 255)
    private String details;
}
