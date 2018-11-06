package io.electrica.connector.hub.dto.sdk;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.electrica.connector.hub.dto.AuthorizationType;
import io.electrica.connector.hub.dto.TokenAuthorizationDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName(AuthorizationType.TOKEN)
public class TokenTypedAuthorizationDto implements TypedAuthorizationDto {

    @NotNull
    private TokenAuthorizationDto data;
}
