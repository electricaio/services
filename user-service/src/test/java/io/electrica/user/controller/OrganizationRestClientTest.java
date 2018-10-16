package io.electrica.user.controller;

import io.electrica.common.exception.BadRequestServiceException;
import io.electrica.user.TestBase;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.rest.OrganizationRestClient;
import io.electrica.user.rest.OrganizationRestClientImpl;
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
public class OrganizationRestClientTest extends TestBase {

    OrganizationRestClient organizationRestClient;
    OrganizationDtoService organizationDtoService;

    @Before
    public void setUp() {
        organizationDtoService = mock(OrganizationDtoService.class);
        organizationRestClient = new OrganizationRestClientImpl(organizationDtoService);
    }

    @Test
    public void createTest() {
        OrganizationDto organizationDto = createNewOrgDto();
        OrganizationDto result = cloneOrgDtoAndAddRevisionNumber(organizationDto);
        when(organizationDtoService.create(organizationDto)).thenReturn(result);
        OrganizationDto actual = organizationRestClient.create(organizationDto).getBody();
        assertNotNull(actual);
        assertEquals(organizationDto.getName(), result.getName());
        assertEquals(organizationDto.getUuid(), result.getUuid());
    }

    @Test(expected = BadRequestServiceException.class)
    public void whenSameNameThrowBadRequestServiceExceptionEx() {
        OrganizationDto organizationDto = createNewOrgDto();
        when(organizationDtoService.create(organizationDto)).thenThrow(BadRequestServiceException.class);
        organizationRestClient.create(organizationDto);
    }

    @Test(expected = RuntimeException.class)
    public void whenPassNullThrowIllegalArgumentException() {
        when(organizationDtoService.create(null)).thenThrow(new RuntimeException());
        organizationRestClient.create(null);
    }

}
