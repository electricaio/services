package io.electrica.user.dto;

import lombok.Getter;
import lombok.Setter;

/**
 *  CreateUserDto is an extention to UserDTO.
 *
 *  @author Munish Sodhi
 */

@Getter
@Setter
public class CreateUserDto extends UserDto {

    private String password;

}
