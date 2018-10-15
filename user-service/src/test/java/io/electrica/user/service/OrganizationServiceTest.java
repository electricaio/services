package io.electrica.user.service;

import io.electrica.user.TestBase;
import io.electrica.user.model.Organization;
import io.electrica.user.repository.OrganizationRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.*;

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


    @Test
    public void getOrAddOrgTestWhenOrgExists(){
        Organization organization = createNewOrg();
        String domain = "test@"+organization.getName();
        when(organizationRepository.findOneByName(organization.getName())).thenReturn(Optional.of(organization));
        Organization actual = organizationService.getOrAdd(domain);
        assertEquals(organization,actual);
    }

    @Test
    public void getOrAddOrgTestWhenOrgNotExists(){
        Organization organization = createNewOrg();
        String domain = "test@"+organization.getName();
        when(organizationRepository.findOneByName(organization.getName())).thenReturn(Optional.empty());
        when(organizationRepository.save(any())).thenReturn(organization);
        Organization actual = organizationService.getOrAdd(domain);
        assertEquals(organization,actual);
    }

}
