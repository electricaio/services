package io.electrica.connector.rest;

import io.electrica.ConnectorServiceApplicationTest;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

public class ConnectorExecutorControllerTest extends ConnectorServiceApplicationTest {

    @Inject
    private ConnectorExecutorControllerImpl connectorExecutorController;

    @Test
    public void name() {
        HttpStatus statusCode = connectorExecutorController.executeAsync(null).getStatusCode();
        assertEquals(HttpStatus.ACCEPTED, statusCode);
    }
}