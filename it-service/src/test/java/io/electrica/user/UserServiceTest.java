package io.electrica.user;

import io.electrica.BaseIT;
import io.electrica.user.dto.OrganizationDto;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class UserServiceTest extends BaseIT {

    @BeforeClass
    public void setup() {
    }

    @Test(groups = { "fillData" })
    public void test() {
        OrganizationDto org = new OrganizationDto();
        org.setName("HackerRank");
        OrganizationDto entity = organizationClient.createIfAbsent(org).getBody();
        assertEquals(entity.getName(), "HackerRank");
    }

}
