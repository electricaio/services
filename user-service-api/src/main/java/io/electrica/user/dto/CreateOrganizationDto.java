package io.electrica.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateOrganizationDto {

    @NotNull
    @NotBlank
    @Max(255)
    private String name;

}
