package io.electrica.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
/**
 *  A DTO representing user with authorities.
 *
 *  @author Munish Sodhi
 */
@Getter
@Setter
public class UserDto {

    private Integer id;
    private Integer version;
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> authorities;

}
