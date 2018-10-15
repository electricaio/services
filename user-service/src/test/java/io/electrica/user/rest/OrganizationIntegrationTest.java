package io.electrica.user.rest;

import io.electrica.user.UserServiceApplicationTest;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.model.Organization;
import io.electrica.user.repository.OrganizationRepository;
import io.electrica.user.service.OrganizationService;
import lombok.NoArgsConstructor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Integration test suit for Organization.
 */
@NoArgsConstructor
public class OrganizationIntegrationTest extends UserServiceApplicationTest {

    @Inject
    OrganizationRestClient organizationRestClient;

    @Inject
    OrganizationRepository organizationRepository;

    @Inject
    OrganizationService organizationService;

    @Before
    public void init() {
    }

    @Test
    public void createOrganizationTest() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test" + new Date().getTime());
        organizationDto.setUuid(UUID.randomUUID());
        OrganizationDto result = organizationRestClient.create(organizationDto).getBody();
        assertNotNull(result);
        assertEquals(organizationDto.getName(), result.getName());
        assertEquals(organizationDto.getUuid(), result.getUuid());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void whenAddOrgWithSameNameThrowException() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test" + new Date().getTime());
        organizationDto.setUuid(UUID.randomUUID());
        organizationRestClient.create(organizationDto);
        organizationRestClient.create(organizationDto);
        organizationRepository.flush();

    }

    @Test(expected = ConstraintViolationException.class)
    public void whenAddOrgWithoutNameThrowException() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName(null);
        organizationDto.setUuid(UUID.randomUUID());
        organizationRestClient.create(organizationDto);
        organizationRepository.flush();
    }

    @Test(expected = ConstraintViolationException.class)
    public void whenAddOrgWithoutUUIDThrowException() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test" + new Date().getTime());
        organizationDto.setUuid(null);
        organizationRestClient.create(organizationDto).getBody();
        organizationRepository.flush();
    }

    @Test
    public void whenCreateOrgTestNew() {
        String domain = "gmail.com";
        Organization result = organizationService.createOrgFromDomain(domain);
        assertNotNull(result);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void whenCreateOrgWithExistingDomainNameThrowsException() {
        String domain = "gmail.com";
        organizationService.createOrgFromDomain(domain);
        organizationService.createOrgFromDomain(domain);
        organizationRepository.flush();
    }

    @Test
    public void whenGetOrAddOrgTestNew() {
        String domain = "gmail.com";
        Organization result = organizationService.createIfAbsent(domain);
        assertNotNull(result);
    }

    @Test
    public void whenGetOrAddOrgTestWithExistingDomain() {
        String domain = "gmail.com";
        Organization result1 = organizationService.createIfAbsent(domain);
        Organization result2 = organizationService.createIfAbsent(domain);
        organizationRepository.flush();
        assertEquals(result1, result2);
    }
}
