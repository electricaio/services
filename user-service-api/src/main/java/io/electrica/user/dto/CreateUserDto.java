package io.electrica.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserDto extends UserDto {

    private String password;

}
