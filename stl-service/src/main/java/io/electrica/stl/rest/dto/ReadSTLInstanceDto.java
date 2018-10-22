package io.electrica.stl.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class ReadSTLInstanceDto {

    @NotNull
    private Long id;

    @NotNull
    private Long revisionVersion;
}
