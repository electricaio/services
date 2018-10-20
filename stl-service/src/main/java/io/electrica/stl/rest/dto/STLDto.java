package io.electrica.stl.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class STLDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Long typeId;

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
