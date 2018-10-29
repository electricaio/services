package io.electrica.connector.hub.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class ConnectDto {

    @NotNull
    public Long connectorId;

    @NotNull
    public Long accessKeyId;

    public ConnectDto(Long connectorId, Long accessKeyId) {
        this.connectorId = connectorId;
        this.accessKeyId = accessKeyId;
    }
}
