package io.electrica.connector.hub.repository;

import io.electrica.ConnectorHubServiceApplicationTest;
import io.electrica.connector.hub.model.AuthorizationType;
import io.electrica.connector.hub.model.ConnectorType;
import io.electrica.connector.hub.util.Fixture;
import org.junit.Before;

import javax.inject.Inject;
import java.util.List;

public abstract class AbstractDatabaseTest extends ConnectorHubServiceApplicationTest implements Fixture {

    @Inject
    protected ConnectionRepository connectionRepository;

    @Inject
    protected ConnectorTypeRepository connectorTypeRepository;

    @Inject
    protected AuthorizationRepository authorizationRepository;

    @Inject
    protected BasicAuthorizationRepository basicAuthorizationRepository;

    @Inject
    protected AuthorizationTypeRepository authorizationTypeRepository;

    @Inject
    protected ConnectorRepository connectorRepository;

    protected List<AuthorizationType> authorizationTypes;

    protected List<ConnectorType> connectorTypes;

    @Before
    public void setup() {
        connectorTypes = connectorTypeRepository.findAll();
        authorizationTypes = authorizationTypeRepository.findAll();
    }

    @Override
    public ConnectorTypeRepository getConnectorTypeRepository() {
        return connectorTypeRepository;
    }

    @Override
    public AuthorizationTypeRepository getAuthorizationTypeRepository() {
        return authorizationTypeRepository;
    }
}
