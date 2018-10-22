package io.electrica;

import io.electrica.test.AbstractJpaApplicationTest;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ConnectorHubServiceApplication.class)
public abstract class ConnectorHubServiceApplicationTest extends AbstractJpaApplicationTest {
}
