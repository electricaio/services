package io.electrica.it;

import io.electrica.ItServiceApplication;
import io.electrica.common.config.EnvironmentTypeConfig;
import io.electrica.connector.hub.feign.AuthorizationClient;
import io.electrica.connector.hub.feign.ConnectionClient;
import io.electrica.connector.hub.feign.ConnectorClient;
import io.electrica.it.auth.TokenManager;
import io.electrica.it.context.ContextHolder;
import io.electrica.user.feign.AccessKeyClient;
import io.electrica.user.feign.OrganizationClient;
import io.electrica.user.feign.UserClient;
import io.electrica.webhook.feign.WebhookClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import javax.inject.Inject;

import static io.electrica.it.util.ItServiceConstants.INIT_GROUP;

@SpringBootTest(classes = ItServiceApplication.class)
@ActiveProfiles(EnvironmentTypeConfig.TEST_ENV_PROFILE_NAME)
public abstract class BaseIT extends AbstractTestNGSpringContextTests {

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

    @Inject
    public AccessKeyClient accessKeyClient;

    @Inject
    public ConnectorClient connectorClient;

    @Inject
    public ConnectionClient connectionClient;

    @Inject
    public AuthorizationClient authorizationClient;

    @Inject
    public WebhookClient webhookClient;

    public Long getCurrTimeInMillSeconds() {
        return System.currentTimeMillis();
    }

    @Test(groups = {INIT_GROUP})
    public void checkMicroservices() {
        // Todo check  microservices are active
    }

}
