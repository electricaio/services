package io.electrica.user.dto;

import com.github.dozermapper.core.Mapping;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CreateUserDto is an extention to UserDTO.
 */

@Getter
@Setter
@NoArgsConstructor
public class CreateUserDto extends UserDto {

    @Mapping("saltedPassword")
    private String password;

    public CreateUserDto(UserDto userDto, String password) {
        super(userDto);
        this.password = password;
    }
}
