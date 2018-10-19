package io.electrica.user.rest;

import io.electrica.user.dto.CreateOrganizationDto;
import io.electrica.user.dto.OrganizationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static io.electrica.common.rest.PathConstants.PUBLIC;
import static io.electrica.common.rest.PathConstants.V1;

/**
 * Organization controller for managing organizations.
 */
public interface OrganizationController {

    @PostMapping(PUBLIC + V1 + "/organizations")
    ResponseEntity<OrganizationDto> createIfAbsent(@RequestBody CreateOrganizationDto organizationDto);

}
