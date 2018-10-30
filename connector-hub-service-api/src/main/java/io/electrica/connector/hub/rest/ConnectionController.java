package io.electrica.connector.hub.rest;

import io.electrica.connector.hub.rest.dto.ConnectDto;
import io.electrica.connector.hub.rest.dto.ConnectionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static io.electrica.common.rest.PathConstants.V1;

@RequestMapping
public interface ConnectionController {

    @GetMapping(V1 + "/users/{userId}/connections")
    ResponseEntity<List<ConnectionDto>> findAllByUser(@PathVariable("userId") Long userId);

    /**
     * Given the connection id and access key id,
     * TODO we are validating if the access key is in the ownership of the session user, via user-service.
     * After that, connection is created containing information about the user and organization as well.
     */
    @PostMapping(V1 + "/connections")
    ResponseEntity<ConnectionDto> connect(ConnectDto request);
}
