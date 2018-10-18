package io.electrica.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * A DTO representing organization.
 */
@Getter
@Setter
@NoArgsConstructor
public class OrganizationDto {

    private Long id;
    private Long revisionVersion;
    private UUID uuid;

    @NotNull
    @NotBlank
    @Max(255)
    private String name;

}
