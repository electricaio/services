package io.electrica.connector.hub.dto.sdk;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.electrica.connector.hub.dto.AuthorizationType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BasicTypedAuthorizationDto.class, name = AuthorizationType.BASIC),
        @JsonSubTypes.Type(value = TokenTypedAuthorizationDto.class, name = AuthorizationType.TOKEN)
})
public interface TypedAuthorizationDto {

}
