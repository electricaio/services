package io.electrica;

import io.electrica.it.auth.TokenManager;
import io.electrica.it.context.ContextHolder;
import io.electrica.user.feign.OrganizationClient;
import io.electrica.user.feign.UserClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import javax.inject.Inject;

@SpringBootTest(classes = ItServiceApplication.class)
public class BaseIT extends AbstractTestNGSpringContextTests {

    public static final String ORG_HACKER_RANK = "HackerRank";
    public static final String ORG_TOP_CODER = "TopCoder";

    @Inject
    public UserClient userClient;

    @Inject
    public OrganizationClient organizationClient;

    @Inject
    public TokenManager tokenManager;

    @Inject
    public ContextHolder contextHolder;

    @Test(groups = {"init"})
    public void checkMicroservices() {
        // Todo check  microservices are active
    }

}
