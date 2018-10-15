package io.electrica.user.service;

import io.electrica.user.TestBase;
import io.electrica.user.model.Organization;
import io.electrica.user.repository.OrganizationRepository;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * OrganizationServiceTest to test rest client for Organization.
 */
public class OrganizationServiceTest extends TestBase {

    OrganizationRepository organizationRepository;
    OrganizationService organizationService;

    @Before
    public void setUp() throws Exception {
        organizationRepository = mock(OrganizationRepository.class);
        organizationService = new OrganizationService(organizationRepository);
    }

    @Test
    public void testCreate() {
        Organization organization = createNewOrg();
        when(organizationService.executeCreate(organization)).thenReturn(organization);
        Organization result = organizationService.executeCreate(organization);
        assertEquals(organization.getName(), result.getName());
        assertEquals(organization.getUuid(), result.getUuid());
    }

}
