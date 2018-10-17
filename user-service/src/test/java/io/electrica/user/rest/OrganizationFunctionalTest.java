package io.electrica.user.rest;

import io.electrica.user.UserServiceApplicationTest;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.repository.OrganizationRepository;
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
 * Functional tests for organization controller.
 */
@NoArgsConstructor
public class OrganizationFunctionalTest extends UserServiceApplicationTest {

    @Inject
    private OrganizationController organizationController;

    @Inject
    private OrganizationRepository organizationRepository;

    @Before
    public void init() {
    }

    @Test
    public void createOrganizationTest() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test" + new Date().getTime());
        organizationDto.setUuid(UUID.randomUUID());
        OrganizationDto result = organizationController.create(organizationDto).getBody();
        assertNotNull(result);
        assertEquals(organizationDto.getName(), result.getName());
        assertEquals(organizationDto.getUuid(), result.getUuid());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void whenAddOrgWithSameNameThrowException() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test" + new Date().getTime());
        organizationDto.setUuid(UUID.randomUUID());
        organizationController.create(organizationDto);
        organizationController.create(organizationDto);
        organizationRepository.flush();

    }

    @Test(expected = ConstraintViolationException.class)
    public void whenAddOrgWithoutNameThrowException() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName(null);
        organizationDto.setUuid(UUID.randomUUID());
        organizationController.create(organizationDto);
        organizationRepository.flush();
    }

    @Test(expected = ConstraintViolationException.class)
    public void whenAddOrgWithoutUUIDThrowException() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test" + new Date().getTime());
        organizationDto.setUuid(null);
        organizationController.create(organizationDto).getBody();
        organizationRepository.flush();
    }
}
