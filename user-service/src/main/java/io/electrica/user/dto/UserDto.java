package io.electrica.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
/**
 *  A DTO representing user with authorities.
 *
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
    private Set<String> roles;

    public UserDto(Integer id, Integer version, String firstName, String lastName, String email, Set<String> authorities, Set<String> roles) {
        this.id = id;
        this.version = version;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.authorities = authorities;
        this.roles = roles;
    }
}
