package io.electrica;

import io.electrica.common.jpa.test.AbstractJpaApplicationTest;
import io.electrica.connector.hub.dto.AuthorizationType;
import io.electrica.connector.hub.dto.CreateConnectorDto;
import io.electrica.connector.hub.model.ConnectorType;
import io.electrica.connector.hub.repository.*;
import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = ConnectorHubServiceApplication.class)
public abstract class ConnectorHubServiceApplicationTest extends AbstractJpaApplicationTest {

    protected static final Map<String, String> TEST_PROPERTIES = new HashMap<String, String>() {{
        put("URL", "www.google.com");
        put("Two", "Two");
        put("Three", "Three");
    }};

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

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setup() {
        connectorTypes = connectorTypeRepository.findAll();
    }

    protected ConnectorTypeRepository getConnectorTypeRepository() {
        return connectorTypeRepository;
    }

    public void flushAndClear() {
        em.flush();
        em.clear();
    }

    protected CreateConnectorDto createHackerRankConnectorDto() {
        final CreateConnectorDto dto = new CreateConnectorDto();
        dto.setName("HackerRank");
        dto.setResource("Applications");
        dto.setNamespace("com.hackerrank");
        dto.setTypeId(
                findConnectorType("Talent").getId()
        );
        dto.setAuthorizationType(AuthorizationType.Token);
        dto.setVersion("1.0");
        dto.setProperties(TEST_PROPERTIES);
        dto.setSourceUrl("https://www.hackerrank.com/api/docs");
        dto.setConnectorUrl("https://localhost:9090");
        dto.setSdkUrl("https://localhost:9091");
        dto.setImageUrl("https://localhost:9091/HackerRankConnetor.png");
        dto.setDescription("Test desc");
        return dto;
    }

    protected CreateConnectorDto createGreenhouseConnectorDto() {
        final CreateConnectorDto dto = new CreateConnectorDto();
        dto.setName("Greenhouse");
        dto.setNamespace("com.greenhouse");
        dto.setTypeId(
                findConnectorType("Talent").getId()
        );
        dto.setAuthorizationType(AuthorizationType.Token);
        dto.setVersion("1.1");
        dto.setProperties(TEST_PROPERTIES);
        dto.setSourceUrl("https://developers.greenhouse.io/harvest.html");
        dto.setConnectorUrl("https://localhost:9090");
        dto.setSdkUrl("https://localhost:9091");
        dto.setImageUrl("https://localhost:9091/GreenHouseConnetor.png");
        dto.setDescription("Test desc");
        return dto;
    }

    protected CreateConnectorDto createMySQLConnectorDto() {
        final CreateConnectorDto dto = new CreateConnectorDto();
        dto.setName("MySQL");
        dto.setNamespace("com.mysql");
        dto.setTypeId(
                findConnectorType("Foundation").getId()
        );
        dto.setAuthorizationType(AuthorizationType.Basic);
        dto.setVersion("5.6");
        dto.setProperties(TEST_PROPERTIES);
        dto.setSourceUrl("https://www.postgresql.org/docs/");
        dto.setConnectorUrl("https://localhost:9090");
        dto.setSdkUrl("https://localhost:9091");
        dto.setImageUrl("https://localhost:9091/PostgresConnetor.png");
        dto.setDescription("Test desc");
        return dto;
    }

    protected ConnectorType findConnectorType(String name) {
        return getConnectorTypeRepository().findAll()
                .stream()
                .filter(st -> st.getName().equals(name))
                .findAny()
                .orElseThrow(() ->
                        new IllegalArgumentException("Connector type with passed name does not exist.")
                );
    }
}
