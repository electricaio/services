package io.electrica.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * A DTO representing organization.
 */
@Getter
@Setter
@NoArgsConstructor
public class OrganizationDto {
    private Long id;
    private UUID uuid;
    private String name;
    private Boolean isActive;
    private Long revisionVersion;

    public OrganizationDto(Long id, UUID uuid, String name, Long revisionVersion) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.revisionVersion = revisionVersion;
    }
}
