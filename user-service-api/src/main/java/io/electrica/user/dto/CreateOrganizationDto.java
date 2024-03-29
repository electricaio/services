package io.electrica.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CreateOrganizationDto {

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String name;

}
