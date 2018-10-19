package io.electrica.user.rest;

import io.electrica.user.dto.CreateOrganizationDto;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.service.OrganizationDtoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * Organization controller implementation.
 */
@RestController
public class OrganizationControllerImpl implements OrganizationController {

    private final Logger logger = LoggerFactory.getLogger(OrganizationControllerImpl.class);

    private final OrganizationDtoService organizationDtoService;

    @Inject
    public OrganizationControllerImpl(OrganizationDtoService organizationDtoService) {
        this.organizationDtoService = organizationDtoService;
    }

    @Override
    public ResponseEntity<OrganizationDto> createIfAbsent(@RequestBody CreateOrganizationDto dto) {
        logger.debug("REST request to save User : {}", dto);
        OrganizationDto result = organizationDtoService.createIfAbsent(dto);
        return ResponseEntity.ok(result);
    }
}
