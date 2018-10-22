package io.electrica.stl.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class ConnectorDto {

    @NotNull
    private Long typeId;

    @NotNull
    private Long authorizationTypeId;

    @NotNull
    @NotBlank
    @Max(255)
    private String name;

    @Max(255)
    private String resource;

    @NotNull
    @NotBlank
    @Max(255)
    private String version;

    @NotNull
    @NotBlank
    @Max(255)
    private String namespace;
}
