package io.electrica.user.dto;

import com.github.dozermapper.core.Mapping;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

/**
 * A DTO representing user with authorities.
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
    @Mapping("organization.id")
    private Long organizationId;
    private Set<String> authorities;
    private Set<RolesDto> roles;
    private Long revisionVersion;

    public UserDto(Long id, UUID uuid, String firstName, String lastName, String email, Long organizationId,
                   Set<String> authorities, Set<RolesDto> roles, Long revisionVersion) {
        this.id = id;
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.organizationId = organizationId;
        this.authorities = authorities;
        this.roles = roles;
        this.revisionVersion = revisionVersion;
    }

    public UserDto(UserDto userDto) {
        this(
                userDto.getId(),
                userDto.getUuid(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getEmail(),
                userDto.getOrganizationId(),
                userDto.getAuthorities(),
                userDto.getRoles(),
                userDto.getRevisionVersion()
        );
    }
}
