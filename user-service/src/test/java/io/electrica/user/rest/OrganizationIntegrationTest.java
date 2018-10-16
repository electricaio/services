package io.electrica.user.rest;

import io.electrica.user.UserServiceApplicationTest;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import lombok.NoArgsConstructor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Integration test suit for Organization.
 */
@NoArgsConstructor
public class OrganizationIntegrationTest extends UserServiceApplicationTest {

    @Before
    public void init() {
        initBaseClass();
    }

    @Test
    public void createOrganizationTest() {
        OrganizationDto organizationDto = createNewOrganization();
        OrganizationDto result = organizationRestClient.create(organizationDto).getBody();
        assertNotNull(result);
        assertEquals(organizationDto.getName(), result.getName());
        assertEquals(organizationDto.getUuid(), result.getUuid());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void whenAddOrgWithSameNameThrowException() {
        OrganizationDto organizationDto = createNewOrganization();
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
}
