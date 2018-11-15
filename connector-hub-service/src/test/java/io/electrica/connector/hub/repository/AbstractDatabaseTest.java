package io.electrica.connector.hub.repository;

import io.electrica.ConnectorHubServiceApplicationTest;
import io.electrica.connector.hub.model.ConnectorType;
import io.electrica.connector.hub.util.Fixture;
import org.junit.Before;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public abstract class AbstractDatabaseTest extends ConnectorHubServiceApplicationTest implements Fixture {

    @PersistenceContext
    private EntityManager em;

    @Inject
    protected ConnectionRepository connectionRepository;

    @Inject
    protected ConnectorTypeRepository connectorTypeRepository;

    @Inject
    protected BasicAuthorizationRepository basicAuthorizationRepository;

    @Inject
    protected TokenAuthorizationRepository tokenAuthorizationRepository;

    @Inject
    protected ConnectorRepository connectorRepository;

    protected List<ConnectorType> connectorTypes;

    @Before
    public void setup() {
        connectorTypes = connectorTypeRepository.findAll();
    }

    @Override
    public ConnectorTypeRepository getConnectorTypeRepository() {
        return connectorTypeRepository;
    }

    public void flushAndClear() {
        em.flush();
        em.clear();
    }

    public void equals(Map<String, String> expected, Map<String, String> actual) {
        if (expected == null && actual == null) {
            return;
        }
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        for (String str : expected.keySet()) {
            assertEquals(expected.get(str), actual.get(str));
        }
    }
}
