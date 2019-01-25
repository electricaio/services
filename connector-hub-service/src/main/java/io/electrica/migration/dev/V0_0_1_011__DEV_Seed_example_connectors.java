package io.electrica.migration.dev;

import io.electrica.common.migration.FlywayApplicationContextBridge;
import io.electrica.connector.hub.dto.AuthorizationType;
import io.electrica.connector.hub.dto.CreateConnectorDto;
import io.electrica.migration.MigrationUtils;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

public class V0_0_1_011__DEV_Seed_example_connectors implements SpringJdbcMigration {

    private static final String SDK_URL = "https://www.electrica.io";
    private static final String SOURCE_URL = "https://www.electrica.io";
    private static final Map<String, String> TEST_PROPERTIES = new HashMap<String, String>() {{
        put("URL", "www.google.com");
        put("Two", "Two");
        put("Three", "Three");
    }};

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) {
        ApplicationContext context = FlywayApplicationContextBridge.instance().getApplicationContext();

        createGreenhouseApplicationConnector(context);
        createSCIMConnector(context);
        createSmartRecruitersApplicationsConnector(context);
        createLeverApplicationsConnector(context);
        createIncomingWebhooksConnector(context);
        createMySQLConnector(context);
        createJenkinsConnector(context);
        createSalesforceConnector(context);
        createMarketoConnector(context);
    }

    private void createGreenhouseApplicationConnector(BeanFactory beanFactory) {
        CreateConnectorDto dto = new CreateConnectorDto(null,
                AuthorizationType.Token, "Greenhouse Applications", "Greenhouse Applications Connector", "greenhouse", "applications", "1".toLowerCase(),
                SOURCE_URL, "https://www.greenhouse.io", SDK_URL,
                "https://images.electrica.io/greenhouse-logo.png",
                TEST_PROPERTIES);
        MigrationUtils.saveConnector(beanFactory, dto, "Talent");
    }

    private void createSCIMConnector(BeanFactory beanFactory) {
        CreateConnectorDto dto = new CreateConnectorDto(null,
                AuthorizationType.Token, "SCIM", "SCIM Connector", "scim", "user", "1".toLowerCase(),
                SOURCE_URL, "https://tools.ietf.org/html/rfc7644", SDK_URL,
                "https://images.electrica.io/scim-logo.png",
                TEST_PROPERTIES);
        MigrationUtils.saveConnector(beanFactory, dto, "Foundation");
    }

    private void createSmartRecruitersApplicationsConnector(BeanFactory beanFactory) {
        CreateConnectorDto dto = new CreateConnectorDto(null,
                AuthorizationType.Token, "SmartRecruiters Applications", "SmartRecruiters Applications Connector", "smartrecruiters", "applications", "1".toLowerCase(),
                SOURCE_URL, "https://www.smartrecruiters.com", SDK_URL,
                "https://images.electrica.io/smartrecruiters-logo.png",
                TEST_PROPERTIES);
        MigrationUtils.saveConnector(beanFactory, dto, "Talent");
    }

    private void createLeverApplicationsConnector(BeanFactory beanFactory) {
        CreateConnectorDto dto = new CreateConnectorDto(null,
                AuthorizationType.Token, "Lever Applications", "Lever Applications Connector", "lever", "applications", "1".toLowerCase(),
                SOURCE_URL, "https://www.lever.co", SDK_URL,
                "https://images.electrica.io/lever-logo.png",
                TEST_PROPERTIES);
        MigrationUtils.saveConnector(beanFactory, dto, "Talent");
    }

    private void createIncomingWebhooksConnector(BeanFactory beanFactory) {
        CreateConnectorDto dto = new CreateConnectorDto(null,
                AuthorizationType.None, "Incoming Webhooks", "Incoming Webhooks Connector", "webhooks", "incoming", "1".toLowerCase(),
                SOURCE_URL, "https://www.electrica.io", SDK_URL,
                "https://images.electrica.io/webhooks-logo.png",
                TEST_PROPERTIES);
        MigrationUtils.saveConnector(beanFactory, dto, "Foundation");
    }

    private void createMySQLConnector(BeanFactory beanFactory) {
        CreateConnectorDto dto = new CreateConnectorDto(null,
                AuthorizationType.Basic, "MySQL", "MySQL Connector", "mysql", "db", "1".toLowerCase(),
                SOURCE_URL, "https://www.mysql.com", SDK_URL,
                "https://images.electrica.io/mysql-logo.png",
                TEST_PROPERTIES);
        MigrationUtils.saveConnector(beanFactory, dto, "Foundation");
    }

    private void createJenkinsConnector(BeanFactory beanFactory) {
        CreateConnectorDto dto = new CreateConnectorDto(null,
                AuthorizationType.Token, "Jenkins", "Jenkins Connector", "jenkins", "jobs", "1".toLowerCase(),
                SOURCE_URL, "https://jenkins.io", SDK_URL,
                "https://images.electrica.io/jenkins-logo.png",
                null);
        MigrationUtils.saveConnector(beanFactory, dto, "Foundation");
    }

    private void createMarketoConnector(BeanFactory beanFactory) {
        CreateConnectorDto dto = new CreateConnectorDto(null,
                AuthorizationType.Token, "Marketo", "Marketo Assets Connector", "marketo", "assets", "1".toLowerCase(),
                SOURCE_URL, "http://marketohost.wpengine.com/rest-api/endpoint-reference/asset-endpoint-reference", SDK_URL,
                "https://images.electrica.io/marketo-logo.png", null);
        MigrationUtils.saveConnector(beanFactory, dto, "CRM");
    }

    private void createSalesforceConnector(BeanFactory beanFactory) {
        CreateConnectorDto dto = new CreateConnectorDto(null,
                AuthorizationType.Token, "Salesforce", "Salesforce Reports Connector", "salesforce", "reports", "1".toLowerCase(),
                SOURCE_URL, "https://developer.salesforce.com/docs/atlas.en-us.api_analytics.meta/api_analytics/sforce_analytics_rest_api_intro.htm", SDK_URL,
                "https://images.electrica.io/salesforce-logo.png", null);
        MigrationUtils.saveConnector(beanFactory, dto, "CRM");
    }
}
