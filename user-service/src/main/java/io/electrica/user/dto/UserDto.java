package io.electrica.user.dto;

import com.github.dozermapper.core.Mapping;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Long revisionVersion;

    public UserDto(Long id, UUID uuid, String firstName, String lastName, String email, Long organizationId,
                   Long revisionVersion) {
        this.id = id;
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.organizationId = organizationId;
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
                userDto.getRevisionVersion()
        );
    }
}
