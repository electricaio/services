package io.electrica.connector.hub.rest;

import io.electrica.connector.hub.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.electrica.common.rest.PathConstants.V1;

public interface AuthorizationController {

    @PostMapping(V1 + "/connections/{connectionId}/authorizations/basic")
    ResponseEntity<BasicAuthorizationDto> authorizeWithBasic(
            @PathVariable("connectionId") Long connectionId,
            @RequestBody CreateBasicAuthorizationDto dto
    );

    @PostMapping(V1 + "/connections/{connectionId}/authorizations/token")
    ResponseEntity<TokenAuthorizationDto> authorizeWithToken(
            @PathVariable("connectionId") Long connectionId,
            @RequestBody CreateTokenAuthorizationDto dto
    );

    @PostMapping(V1 + "/connections/{connectionId}/authorizations/ibm")
    ResponseEntity<IbmAuthorizationDto> authorizeWithIbm(
            @PathVariable("connectionId") Long connectionId,
            @RequestBody CreateIbmAuthorizationDto dto
    );


    @GetMapping(V1 + "/authorizations/{id}/basic")
    ResponseEntity<BasicAuthorizationDto> getBasic(@PathVariable("id") Long id);

    @GetMapping(V1 + "/authorizations/{id}/token")
    ResponseEntity<TokenAuthorizationDto> getToken(@PathVariable("id") Long id);

    @GetMapping(V1 + "/authorizations/{id}/ibm")
    ResponseEntity<IbmAuthorizationDto> getIbm(@PathVariable("id") Long id);


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

    @PutMapping(V1 + "/authorizations/{id}/ibm")
    ResponseEntity<IbmAuthorizationDto> updateIbm(
            @PathVariable("id") Long id,
            @RequestBody IbmAuthorizationDto dto
    );
}
