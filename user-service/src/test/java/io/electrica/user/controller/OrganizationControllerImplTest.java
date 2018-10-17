package io.electrica.user.controller;

import io.electrica.common.exception.BadRequestServiceException;
import io.electrica.user.TestBase;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.rest.OrganizationController;
import io.electrica.user.rest.OrganizationControllerImpl;
import io.electrica.user.service.OrganizationDtoService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for Organization Rest Controller.
 */
public class OrganizationControllerImplTest extends TestBase {

    OrganizationController organizationController;
    OrganizationDtoService organizationDtoService;

    @Before
    public void setUp() {
        organizationDtoService = mock(OrganizationDtoService.class);
        organizationController = new OrganizationControllerImpl(organizationDtoService);
    }

    @Test
    public void createTest() {
        OrganizationDto organizationDto = createNewOrgDto();
        OrganizationDto result = cloneOrgDtoAndAddRevisionNumber(organizationDto);
        when(organizationDtoService.create(organizationDto)).thenReturn(result);
        OrganizationDto actual = organizationController.create(organizationDto).getBody();
        assertNotNull(actual);
        assertEquals(organizationDto.getName(), result.getName());
        assertEquals(organizationDto.getUuid(), result.getUuid());
    }

    @Test(expected = BadRequestServiceException.class)
    public void whenSameNameThrowBadRequestServiceExceptionEx() {
        OrganizationDto organizationDto = createNewOrgDto();
        when(organizationDtoService.create(organizationDto)).thenThrow(BadRequestServiceException.class);
        organizationController.create(organizationDto);
    }

    @Test(expected = RuntimeException.class)
    public void whenPassNullThrowIllegalArgumentException() {
        when(organizationDtoService.create(null)).thenThrow(new RuntimeException());
        organizationController.create(null);
    }

}
