package io.electrica.user.rest;

import io.electrica.user.UserServiceApplicationTest;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.repository.OrganizationRepository;
import lombok.NoArgsConstructor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    OrganizationRestClient organizationRestClient;

    @Autowired
    OrganizationRepository organizationRepository;

    @Before
    public void init() {
    }

    @Test
    public void createOrganizationTest() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test" + new Date().getTime());
        organizationDto.setIsActive(Boolean.TRUE);
        organizationDto.setUuid(UUID.randomUUID());
        OrganizationDto result = organizationRestClient.create(organizationDto).getBody();
        assertNotNull(result);
        assertEquals(organizationDto.getName(), result.getName());
        assertEquals(organizationDto.getUuid(), result.getUuid());
        assertEquals(organizationDto.getIsActive(), result.getIsActive());
    }

    @Test(expected = DataIntegrityViolationException.class)
    @Transactional
    public void whenAddOrgWithSameNameThrowException() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test" + new Date().getTime());
        organizationDto.setIsActive(Boolean.TRUE);
        organizationDto.setUuid(UUID.randomUUID());
        organizationRestClient.create(organizationDto);
        organizationRestClient.create(organizationDto);
        organizationRepository.flush();

    }

    @Transactional
    @Test(expected = ConstraintViolationException.class)
    public void whenAddOrgWithoutNameThrowException() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName(null);
        organizationDto.setIsActive(Boolean.TRUE);
        organizationDto.setUuid(UUID.randomUUID());
        organizationRestClient.create(organizationDto);
        organizationRepository.flush();
    }

    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void whenAddOrgWithoutUUIDThrowException() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test" + new Date().getTime());
        organizationDto.setIsActive(Boolean.TRUE);
        organizationDto.setUuid(null);
        organizationRestClient.create(organizationDto).getBody();
        organizationRepository.flush();
    }

    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void whenAddOrgWithoutIsActiveThrowException() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test" + new Date().getTime());
        organizationDto.setIsActive(null);
        organizationDto.setUuid(UUID.randomUUID());
        organizationRestClient.create(organizationDto).getBody();
        organizationRepository.flush();
    }
}
