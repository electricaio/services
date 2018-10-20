package io.electrica.stl.repository;

import io.electrica.STLServiceApplicationTest;
import io.electrica.stl.model.AuthorizationType;
import io.electrica.stl.model.ConnectorType;
import io.electrica.stl.util.Fixture;
import org.junit.Before;

import javax.inject.Inject;
import java.util.List;

public abstract class AbstractDatabaseTest extends STLServiceApplicationTest implements Fixture {

    @Inject
    protected ConnectionRepository connectionRepository;

    @Inject
    protected ConnectorTypeRepository connectorTypeRepository;

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
