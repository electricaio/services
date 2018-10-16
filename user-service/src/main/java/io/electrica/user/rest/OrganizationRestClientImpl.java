package io.electrica.user.rest;

import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.service.OrganizationDtoService;
import io.electrica.user.service.UserDtoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Client implementation for Managing organizations..
 */
@RestController
public class OrganizationRestClientImpl implements OrganizationRestClient {

    private final Logger logger = LoggerFactory.getLogger(OrganizationRestClientImpl.class);

    private final OrganizationDtoService organizationDtoService;

    public OrganizationRestClientImpl(OrganizationDtoService organizationDtoService) {
        this.organizationDtoService = organizationDtoService;
    }

    @Override
    public ResponseEntity<OrganizationDto> create(@RequestBody OrganizationDto organizationDto) {
        logger.debug("REST request to save User : {}", organizationDto);
        OrganizationDto result = organizationDtoService.create(organizationDto);
        return ResponseEntity.ok(result);
    }

}
