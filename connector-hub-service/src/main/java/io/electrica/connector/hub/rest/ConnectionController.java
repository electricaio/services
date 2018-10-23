package io.electrica.connector.hub.rest;

import io.electrica.connector.hub.rest.dto.ConnectDto;
import io.electrica.connector.hub.rest.dto.ConnectionDto;
import io.electrica.connector.hub.rest.dto.CreateBasicAuthorizationDto;
import io.electrica.connector.hub.rest.dto.ReadAuthorizationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static io.electrica.common.rest.PathConstants.V1;

@RequestMapping(V1 + "/connections")
public interface ConnectionController {

    /**
     * Given the connection id and access key id,
     * TODO we are validating if the access key is in the ownership of the session user, via user-service.
     * After that, connection is created containing information about the user and organization as well.
     */
    @PostMapping
    ResponseEntity<ConnectionDto> connect(ConnectDto request);

    /**
     * Given the authorization data,
     * we are persisting authorization details required to do a basic auth on our behalf.
     * <p>
     * If a user has already provided details and invokes authorization with new credentials,
     * they will replace the old one.
     */
    @PostMapping("/{connectionId}/authorizations/basic")
    ResponseEntity<ReadAuthorizationDto> authorize(@PathVariable("connectionId") Long connectionId,
                                                   CreateBasicAuthorizationDto request);
}
