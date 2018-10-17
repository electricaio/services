package io.electrica.user.dto;

import com.github.dozermapper.core.Mapper;
import io.electrica.user.TestBase;
import io.electrica.user.model.Organization;
import io.electrica.user.service.OrganizationDtoService;
import io.electrica.user.service.OrganizationService;
import io.electrica.user.service.UserDtoService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for OrganizationDtoService.
 */
public class OrganizationDtoServiceTest extends TestBase {

    OrganizationDtoService organizationDtoService;
    OrganizationService organizationService;
    UserDtoService userDtoService;

    Mapper mapper = mock(Mapper.class);

    @Before
    public void setUp() throws Exception {
        organizationService = mock(OrganizationService.class);
        userDtoService = mock(UserDtoService.class);
        organizationDtoService = new OrganizationDtoService(organizationService);
        org.springframework.test.util.ReflectionTestUtils.setField(
                organizationDtoService, "mapper", mapper
        );
    }

    @Test
    public void createTest() {
        OrganizationDto organizationDto = createNewOrgDto();
        OrganizationDto organizationDtoWithRev = cloneOrgDtoAndAddRevisionNumber(organizationDto);
        Organization organization = toEntity(organizationDto);
        when(organizationService.create(organization)).thenReturn(organization);
        when(mapper.map(organizationDto, Organization.class)).thenReturn(organization);
        when(mapper.map(organization, OrganizationDto.class)).thenReturn(organizationDtoWithRev);
        OrganizationDto result = organizationDtoService.create(organizationDto);
        assertNotNull(result);
        assertEquals(organization.getName(), result.getName());
        assertEquals(organization.getUuid(), result.getUuid());
    }
}
