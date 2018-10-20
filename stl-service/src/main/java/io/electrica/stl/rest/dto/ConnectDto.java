package io.electrica.stl.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class ConnectDto {

    @NotNull
    public Long connectorId;

    @NotNull
    public Long accessKeyId;

    @Valid
    @NotNull
    private AuthorizationDto authorization;

    public ConnectDto(Long connectorId, Long accessKeyId, AuthorizationDto authorization) {
        this.connectorId = connectorId;
        this.accessKeyId = accessKeyId;
        this.authorization = authorization;
    }
}
