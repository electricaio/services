package io.electrica.stl.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReadSTLDto extends STLDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String ern;

    private Long revisionVersion;
}
