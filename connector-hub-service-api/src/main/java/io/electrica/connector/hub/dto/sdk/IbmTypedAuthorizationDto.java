package io.electrica.connector.hub.dto.sdk;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.electrica.connector.hub.dto.AuthorizationType;
import io.electrica.connector.hub.dto.IbmAuthorizationDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName(AuthorizationType.IBM)
public class IbmTypedAuthorizationDto implements TypedAuthorizationDto {

    @NotNull
    private IbmAuthorizationDto data;
}
