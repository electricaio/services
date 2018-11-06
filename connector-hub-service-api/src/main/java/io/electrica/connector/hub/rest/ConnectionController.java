package io.electrica.connector.hub.rest;

import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.CreateConnectionDto;
import io.electrica.connector.hub.dto.sdk.FullConnectionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.electrica.common.rest.PathConstants.PRIVATE;
import static io.electrica.common.rest.PathConstants.V1;

public interface ConnectionController {

    @GetMapping(V1 + "/users/{userId}/connections")
    ResponseEntity<List<ConnectionDto>> findAllByUser(@PathVariable("userId") Long userId);

    /**
     * Given the connection id and access key id,
     * After that, connection is created containing information about the user and organization as well.
     */
    @PostMapping(V1 + "/connections")
    ResponseEntity<ConnectionDto> create(@RequestBody CreateConnectionDto dto);

    @GetMapping(V1 + "/connections/{id}")
    ResponseEntity<ConnectionDto> get(@PathVariable("id") Long id);

    @GetMapping(PRIVATE + V1 + "/connections/{id}")
    ResponseEntity<FullConnectionDto> getFull(@PathVariable("id") Long id);

    @GetMapping(PRIVATE + V1 + "/connections/me")
    ResponseEntity<List<ConnectionDto>> findAllByMe(
            @RequestParam(value = "connectionName", required = false) String connectionName
    );

}
