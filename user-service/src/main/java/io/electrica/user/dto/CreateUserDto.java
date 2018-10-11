package io.electrica.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *  CreateUserDto is an extention to UserDTO.
 *
 */

@Getter
@Setter
@NoArgsConstructor
public class CreateUserDto extends UserDto {

    private String saltedPassword;

    public CreateUserDto(UserDto userDto, String saltedPassword) {
        super(userDto);
        this.saltedPassword = saltedPassword;
    }
}
