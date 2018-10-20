package io.electrica.stl.repository;

import io.electrica.stl.model.ConnectorType;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class ConnectorTypeRepositoryTest extends AbstractDatabaseTest {

    @Test(expected = DataIntegrityViolationException.class)
    public void testCreateConnectorTypeWithSameName() {
//        setup
        connectorTypeRepository.saveAndFlush(
                new ConnectorType("Test")
        );

//        method
        connectorTypeRepository.saveAndFlush(
                new ConnectorType("Test")
        );
    }
}
