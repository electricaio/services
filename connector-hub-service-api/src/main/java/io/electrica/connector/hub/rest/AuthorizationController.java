package io.electrica.connector.hub.rest;

import io.electrica.connector.hub.rest.dto.CreateBasicAuthorizationDto;
import io.electrica.connector.hub.rest.dto.CreateTokenAuthorizationDto;
import io.electrica.connector.hub.rest.dto.ReadAuthorizationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import static io.electrica.common.rest.PathConstants.V1;

public interface AuthorizationController {

    @GetMapping(V1 + "/connections/{connectionId}/authorizations")
    ResponseEntity<List<ReadAuthorizationDto>> findAll(@PathVariable("connectionId") Long connectionId);

    /**
     * Given the authorization data,
     * we are persisting authorization details required to do a basic auth on our behalf.
     * <p>
     * If a user has already provided details and invokes authorization with new credentials,
     * they will replace the old one.
     */
    @PostMapping(V1 + "/connections/{connectionId}/authorizations/basic")
    ResponseEntity<ReadAuthorizationDto> authorizeWithUserAndPassword(@PathVariable("connectionId") Long connectionId,
                                                                      CreateBasicAuthorizationDto request);

    /**
     * Given the authorization data,
     * we are persisting authorization details required to do a token auth on our behalf.
     * <p>
     * If a user has already provided details and invokes authorization with new credentials,
     * they will replace the old one.
     */
    @PostMapping(V1 + "/connections/{connectionId}/authorizations/token")
    ResponseEntity<ReadAuthorizationDto> authorizeWithToken(@PathVariable("connectionId") Long connectionId,
                                                            CreateTokenAuthorizationDto request);
}
