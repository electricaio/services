package io.electrica.connector.hub.dto.sdk;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.electrica.connector.hub.dto.AuthorizationType;
import io.electrica.connector.hub.dto.BasicAuthorizationDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName(AuthorizationType.BASIC)
public class BasicTypedAuthorizationDto implements TypedAuthorizationDto {

    @NotNull
    private BasicAuthorizationDto data;
}
