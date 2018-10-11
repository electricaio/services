package io.electrica.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 *  A DTO representing organization.
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class OrganizationDto {
    private Long id;
    private UUID uuid;
    private String name;

    public OrganizationDto(Long id, UUID uuid, String name) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
    }
}
