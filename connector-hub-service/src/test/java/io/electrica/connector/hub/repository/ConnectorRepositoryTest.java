package io.electrica.connector.hub.repository;

import io.electrica.common.helper.ERNUtils;
import io.electrica.connector.hub.model.Connector;
import io.electrica.connector.hub.model.ConnectorType;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static io.electrica.connector.hub.model.enums.AuthorizationTypeName.BASIC_AUTHORIZATION;

public class ConnectorRepositoryTest extends AbstractDatabaseTest {

    /**
     * Tests a case when resource is not provided and adding
     * 2 Connectors with same name should raise constraint exception on ERN,
     * since they would end up being same.
     * */
    @Test(expected = DataIntegrityViolationException.class)
    public void testSaveConnectorWithoutResourceResultingInSameERN() {

        final ConnectorType type = findConnectorType("Foundation");

        final String name = "MySQL";
        final String namespace = "com.mysql";
        final String version = "5.6";
        final String ern = ERNUtils.createERN(name, version);

        final Connector first = new Connector();
        first.setType(type);
        first.setName(name);
        first.setVersion(version);
        first.setNamespace(namespace);
        first.setErn(ern);
        first.setAuthorizationType(
                findAuthorizationType(BASIC_AUTHORIZATION)
        );
        connectorRepository.saveAndFlush(first);

        final Connector second = new Connector();
        second.setType(type);
        second.setName(name);
        second.setVersion(version);
        second.setNamespace(namespace);
        second.setErn(ern);
        second.setAuthorizationType(
                findAuthorizationType(BASIC_AUTHORIZATION)
        );
        connectorRepository.saveAndFlush(second);
    }
}
