package io.electrica.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 *  A DTO representing user with authorities.
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class UserDto {

    private Long id;
    private UUID uuid;
    private String firstName;
    private String lastName;
    private String email;
    private OrganizationDto organization;
    private Set<String> authorities;
    private Set<RolesDto> roles;
    private Instant createdAt;
    private Instant updatedAt;

    public UserDto(Long id, UUID uuid, String firstName, String lastName, String email, OrganizationDto organization, Set<String> authorities, Set<RolesDto> roles, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.organization = organization;
        this.authorities = authorities;
        this.roles = roles;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UserDto(UserDto userDto) {
        this(
                userDto.getId(),
                userDto.getUuid(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getEmail(),
                userDto.getOrganization(),
                userDto.getAuthorities(),
                userDto.getRoles(),
                userDto.getCreatedAt(),
                userDto.getUpdatedAt()
        );
    }
}
