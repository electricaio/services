package io.electrica.connector.hub.rest;

import io.electrica.connector.hub.dto.BasicAuthorizationDto;
import io.electrica.connector.hub.dto.CreateBasicAuthorizationDto;
import io.electrica.connector.hub.dto.CreateTokenAuthorizationDto;
import io.electrica.connector.hub.dto.TokenAuthorizationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.electrica.common.rest.PathConstants.V1;

public interface AuthorizationController {

    /**
     * Given the authorization data,
     * we are persisting authorization details required to do a basic auth on our behalf.
     * <p>
     * If a user has already provided details and invokes authorization with new credentials,
     * they will replace the old one.
     */
    @PostMapping(V1 + "/connections/{connectionId}/authorizations/basic")
    ResponseEntity<BasicAuthorizationDto> authorizeWithBasic(
            @PathVariable("connectionId") Long connectionId,
            @RequestBody CreateBasicAuthorizationDto dto
    );

    /**
     * Given the authorization data,
     * we are persisting authorization details required to do a token auth on our behalf.
     * <p>
     * If a user has already provided details and invokes authorization with new credentials,
     * they will replace the old one.
     */
    @PostMapping(V1 + "/connections/{connectionId}/authorizations/token")
    ResponseEntity<TokenAuthorizationDto> authorizeWithToken(
            @PathVariable("connectionId") Long connectionId,
            @RequestBody CreateTokenAuthorizationDto dto
    );

    @GetMapping(V1 + "/authorizations/{id}/basic")
    ResponseEntity<BasicAuthorizationDto> getBasic(@PathVariable("id") Long id);

    @GetMapping(V1 + "/authorizations/{id}/token")
    ResponseEntity<TokenAuthorizationDto> getToken(@PathVariable("id") Long id);

    @PutMapping(V1 + "/authorizations/{id}/basic")
    ResponseEntity<BasicAuthorizationDto> updateBasic(
            @PathVariable("id") Long id,
            @RequestBody BasicAuthorizationDto dto
    );

    @PutMapping(V1 + "/authorizations/{id}/token")
    ResponseEntity<TokenAuthorizationDto> updateToken(
            @PathVariable("id") Long id,
            @RequestBody TokenAuthorizationDto dto
    );
}
