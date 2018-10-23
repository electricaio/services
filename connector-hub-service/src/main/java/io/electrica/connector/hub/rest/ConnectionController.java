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

    @PostMapping
    ResponseEntity<ConnectionDto> connect(ConnectDto request);

    @PostMapping("/{connectionId}/authorizations/basic")
    ResponseEntity<ReadAuthorizationDto> authorize(@PathVariable("connectionId") Long connectionId,
                                                   CreateBasicAuthorizationDto request);
}
