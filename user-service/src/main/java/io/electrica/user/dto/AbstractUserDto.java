package io.electrica.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractUserDto {

    private String email;
    private String firstName;
    private String lastName;
    private Long organizationId;

}
