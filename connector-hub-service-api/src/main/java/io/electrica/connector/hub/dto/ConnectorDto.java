package io.electrica.connector.hub.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class ConnectorDto extends CreateConnectorDto {

    @NotNull
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String ern;

    @NotNull
    private Long revisionVersion;
}
