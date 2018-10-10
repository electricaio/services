package io.electrica.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *  A DTO for the Role Entity.
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class RolesDto {

    private Long id;
    private String name;
    private String description;

    public RolesDto(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
