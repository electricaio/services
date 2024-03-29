package io.electrica.connector.hub.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class IbmAuthorizationDto extends CreateIbmAuthorizationDto {

    @NotNull
    private Long id;

    @NotNull
    private Long revisionVersion;
}
