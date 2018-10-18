package io.electrica.user.rest;

import io.electrica.user.UserServiceApplicationTest;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.repository.OrganizationRepository;
import lombok.NoArgsConstructor;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for organization controller.
 */
@NoArgsConstructor
public class OrganizationControllerTest extends UserServiceApplicationTest {

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
        OrganizationDto result = organizationController.createIfAbsent(organizationDto).getBody();
        assertNotNull(result);
        assertEquals(organizationDto.getName(), result.getName());
        assertEquals(organizationDto.getUuid(), result.getUuid());
    }

    @Test(expected = ConstraintViolationException.class)
    public void whenAddOrgWithTooLongNameThrowException() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("" +
                "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345" +
                "678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901" +
                "2345678901234567890123456789012345678901234567890123456789012345");
        organizationDto.setUuid(UUID.randomUUID());
        organizationController.createIfAbsent(organizationDto);
        organizationRepository.flush();

    }

    @Test(expected = ConstraintViolationException.class)
    public void whenAddOrgWithoutNameThrowException() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName(null);
        organizationDto.setUuid(UUID.randomUUID());
        organizationController.createIfAbsent(organizationDto);
        organizationRepository.flush();
    }

    @Test(expected = ConstraintViolationException.class)
    public void whenAddOrgWithoutUUIDThrowException() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test" + new Date().getTime());
        organizationDto.setUuid(null);
        organizationController.createIfAbsent(organizationDto).getBody();
        organizationRepository.flush();
    }
}
