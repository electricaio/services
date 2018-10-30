package io.electrica.user.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * A DTO representing organization.
 */
@Getter
@Setter
public class OrganizationDto extends CreateOrganizationDto {

    private Long id;
    private Long revisionVersion;

}
