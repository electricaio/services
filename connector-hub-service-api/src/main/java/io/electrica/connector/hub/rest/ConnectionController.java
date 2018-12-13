package io.electrica.connector.hub.rest;

import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.CreateConnectionDto;
import io.electrica.connector.hub.dto.sdk.FullConnectionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.electrica.common.rest.PathConstants.PRIVATE;
import static io.electrica.common.rest.PathConstants.V1;

public interface ConnectionController {

    @GetMapping(V1 + "/users/{userId}/connections")
    ResponseEntity<List<ConnectionDto>> findAllByUser(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "connectorId", required = false) @Nullable Long connectorId
    );

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

    @GetMapping(PRIVATE + V1 + "/me/connections")
    ResponseEntity<List<ConnectionDto>> findAllByAccessKey(
            @RequestParam(value = "connectionName", required = false) @Nullable String connectionName,
            @RequestParam(value = "ern") String ern
    );

    @GetMapping(V1 + "/access-keys/{accessKeyId}/connections")
    ResponseEntity<List<ConnectionDto>> findAllByAccessKeyId(@PathVariable("accessKeyId") Long accessKeyId);

    @GetMapping(PRIVATE + V1 + "/me/connections/{connectionId}/validate")
    ResponseEntity<Boolean> connectionBelongsCurrentUser(@PathVariable("connectionId") Long connectionId);

    @DeleteMapping(V1 + "/connections/{connectionId}")
    ResponseEntity<ConnectionDto> delete(@PathVariable("connectionId") Long connectionId);

}
