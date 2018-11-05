package io.electrica.connector.hub.dto.sdk;

import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.ConnectorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FullConnectionDto {

    @NotNull
    private ConnectionDto connection;

    @NotNull
    private ConnectorDto connector;

    @NotNull
    private TypedAuthorizationDto authorization;

}
