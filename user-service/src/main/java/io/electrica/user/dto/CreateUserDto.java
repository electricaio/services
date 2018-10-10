package io.electrica.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 *  CreateUserDto is an extention to UserDTO.
 *
 */

@Getter
@Setter
@NoArgsConstructor
public class CreateUserDto extends UserDto {

    private String password;

    public CreateUserDto(Long id, UUID uuid, String firstName, String lastName, String email, String organization, Set<String> authorities, Set<RolesDto> roles, Instant createdAt, Instant updatedAt, String password) {
        super(id, uuid, firstName, lastName, email, organization, authorities, roles, createdAt, updatedAt);
        this.password = password;
    }
}
