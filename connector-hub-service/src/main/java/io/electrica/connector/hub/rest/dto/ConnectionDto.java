package io.electrica.connector.hub.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class ConnectionDto {

    @NotNull
    private Long id;

    @NotNull
    private Long accessKeyId;

    @NotNull
    private Long connectorId;

    @NotNull
    private Long revisionVersion;
}
