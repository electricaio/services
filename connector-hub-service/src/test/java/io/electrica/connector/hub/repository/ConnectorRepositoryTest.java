package io.electrica.connector.hub.repository;

import io.electrica.common.helper.ERNUtils;
import io.electrica.connector.hub.dto.AuthorizationType;
import io.electrica.connector.hub.model.Connector;
import io.electrica.connector.hub.model.ConnectorType;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class ConnectorRepositoryTest extends AbstractDatabaseTest {

    /**
     * Tests a case when resource is not provided and adding
     * 2 Connectors with same name should raise constraint exception on ERN,
     * since they would end up being same.
     */
    @Test(expected = DataIntegrityViolationException.class)
    public void testSaveConnectorWithoutResourceResultingInSameERN() {

        final Connector first = createConnector();
        connectorRepository.saveAndFlush(first);

        final Connector second = createConnector();
        connectorRepository.saveAndFlush(second);
    }

    private Connector createConnector() {
        final ConnectorType type = findConnectorType("Foundation");
        final String name = "MySQL";
        final String version = "5.6";
        final String ern = ERNUtils.createERN(name, version);
        Connector connector = new Connector();
        connector.setType(type);
        connector.setName(name);
        connector.setVersion(version);
        connector.setNamespace("com.mysql");
        connector.setErn(ern);
        connector.setAuthorizationType(AuthorizationType.Basic);
        connector.setSourceUrl("https://www.hackerrank.com/api/docs");
        connector.setConnectorUrl("https://localhost:9090");
        connector.setSdkUrl("https://localhost:9091");
        connector.setImageUrl("https://localhost:9091/HackerRankConnetor.png");
        return connector;
    }
}
