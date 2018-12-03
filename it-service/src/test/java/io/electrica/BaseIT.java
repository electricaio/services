package io.electrica;

import io.electrica.user.feign.OrganizationClient;
import io.electrica.user.feign.UserClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import javax.inject.Inject;

@SpringBootTest(classes = ItServiceApplication.class)
public class BaseIT extends AbstractTestNGSpringContextTests {

    @Inject
    public UserClient userClient;

    @Inject
    public OrganizationClient organizationClient;
}
