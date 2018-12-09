package io.electrica.connector.hub.rest;

import io.electrica.connector.hub.dto.CreateConnectorDto;
import io.electrica.connector.hub.dto.ConnectorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static io.electrica.common.rest.PathConstants.PUBLIC;
import static io.electrica.common.rest.PathConstants.V1;

public interface ConnectorController {

    @GetMapping(PUBLIC + V1 + "/connectors")
    ResponseEntity<List<ConnectorDto>> findAll();

    @GetMapping(V1 + "/connectors/{id}")
    ResponseEntity<ConnectorDto> getConnector(@PathVariable("id") Long id);

    @PostMapping(V1 + "/connectors")
    ResponseEntity<ConnectorDto> create(@RequestBody CreateConnectorDto dto);

}
