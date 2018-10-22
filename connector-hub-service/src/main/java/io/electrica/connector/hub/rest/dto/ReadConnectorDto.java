package io.electrica.connector.hub.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class ReadConnectorDto extends ConnectorDto {

    @NotNull
    private Long id;

    @NotBlank
    @Max(255)
    private String ern;

    @NotNull
    private Long revisionVersion;
}
