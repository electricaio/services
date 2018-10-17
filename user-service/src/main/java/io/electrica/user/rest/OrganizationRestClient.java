package io.electrica.user.rest;

import io.electrica.user.dto.OrganizationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static io.electrica.common.rest.PathConstants.PUBLIC;
import static io.electrica.common.rest.PathConstants.V1;

/**
 * REST Client for Managing organizations.
 */
public interface OrganizationRestClient {
    @PostMapping(V1 + "/organizations")
    ResponseEntity<OrganizationDto> create(@RequestBody OrganizationDto organizationDto);

}
