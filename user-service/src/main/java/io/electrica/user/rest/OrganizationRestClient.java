package io.electrica.user.rest;

import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static io.electrica.common.rest.PathConstants.PUBLIC;
import static io.electrica.common.rest.PathConstants.V1;

/**
 * REST Client for Managing organizations.
 */
public interface OrganizationRestClient {
    @PostMapping(PUBLIC + V1 + "/organizations")
    ResponseEntity<OrganizationDto> create(@RequestBody OrganizationDto organizationDto);

}
