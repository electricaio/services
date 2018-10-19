package io.electrica.user.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * A DTO representing user with authorities.
 */
@Getter
@Setter
public class UserDto extends AbstractUserDto {

    private Long id;
    private Long revisionVersion;

}
