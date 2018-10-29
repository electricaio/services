package io.electrica.connector.hub.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class ReadAuthorizationDto extends AuthorizationDto {

    @NotNull
    private Long id;

    @NotNull
    private Long revisionVersion;
}
