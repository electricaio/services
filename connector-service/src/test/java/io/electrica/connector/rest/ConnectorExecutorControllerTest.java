package io.electrica.connector.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.electrica.ConnectorServiceApplicationTest;
import io.electrica.connector.dto.ConnectorExecutorContext;
import io.electrica.connector.dto.ConnectorExecutorResult;
import io.electrica.connector.dto.ErrorDto;
import io.electrica.connector.dto.InvocationContext;
import io.electrica.connector.hub.dto.AuthorizationType;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.ConnectorDto;
import io.electrica.connector.hub.dto.sdk.FullConnectionDto;
import io.electrica.integration.spi.exception.ExceptionCodes;
import io.electrica.test.context.ForAccessKey;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.*;

public class ConnectorExecutorControllerTest extends ConnectorServiceApplicationTest {

    @Inject
    private ConnectorExecutorControllerImpl connectorExecutorController;

    @Inject
    private ObjectMapper mapper;

    private static void assertCommonResult(ConnectorExecutorContext context, ConnectorExecutorResult result) {
        assertNotNull(result);
        assertEquals(context.getInvocationId(), result.getInvocationId());
        assertEquals(context.getInvocationContext().getInstanceId(), result.getInstanceId());
        assertEquals(context.getConnection().getConnection().getId(), result.getConnectionId());
        assertEquals(context.getInvocationContext().getConnectionId(), result.getConnectionId());
    }

    @Test
    @ForAccessKey(userId = 1, accessKeyId = 1)
    public void executeSyncTest() throws JsonProcessingException {
        ConnectorExecutorContext context = createConnectorExecutorContext(
                new TestGoogleSearchConnectorExecutor.SearchParameters(10),
                new TestGoogleSearchConnectorExecutor.SearchPayload("test+query")
        );

        ConnectorExecutorResult result = connectorExecutorController.executeSync(context).getBody();
        assertTrue(TestGoogleSearchConnectorExecutor.AFTER_LOAD_METHOD.get());

        assertCommonResult(context, result);
        assertEquals(true, result.getSuccess());
        assertNull(result.getError());

        JsonNode r = result.getResult();
        assertNotNull(r);
        TestGoogleSearchConnectorExecutor.SearchResult sr =
                mapper.treeToValue(r, TestGoogleSearchConnectorExecutor.SearchResult.class);
        assertEquals(sr.getCode(), (Integer) TestGoogleSearchConnectorExecutor.RESPONSE_CODE.get());
        assertNotNull(sr.getData());
    }

    @Test
    @ForAccessKey(userId = 1, accessKeyId = 1)
    public void executeSyncValidationFailTest() {
        ConnectorExecutorContext context = createConnectorExecutorContext(
                new TestGoogleSearchConnectorExecutor.SearchParameters(), // shouldn't be empty
                new TestGoogleSearchConnectorExecutor.SearchPayload("test+query")
        );

        ConnectorExecutorResult result = connectorExecutorController.executeSync(context).getBody();
        assertTrue(TestGoogleSearchConnectorExecutor.AFTER_LOAD_METHOD.get());

        assertCommonResult(context, result);
        assertEquals(false, result.getSuccess());
        assertNull(result.getResult());

        ErrorDto e = result.getError();
        assertNotNull(e);
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
        assertNotNull(e.getStackTrace());
        assertNull(e.getPayload());
    }

    private ConnectorExecutorContext createConnectorExecutorContext(Object parameters, Object payload) {
        long connectionId = 1000L;
        UUID invocationId = UUID.randomUUID();
        InvocationContext ic = new InvocationContext();
        ic.setInstanceId(UUID.randomUUID());
        ic.setConnectionId(connectionId);
        ic.setParameters(mapper.convertValue(parameters, JsonNode.class));
        ic.setPayload(mapper.convertValue(payload, JsonNode.class));

        ConnectorDto connector = new ConnectorDto();
        connector.setId(1000L);
        connector.setRevisionVersion(0L);
        connector.setErn(TestGoogleSearchConnectorExecutor.ERN);
        connector.setAuthorizationType(AuthorizationType.None);
        connector.setProperties(Collections.singletonMap(
                TestGoogleSearchConnectorExecutor.SEARCH_ADD_INTERCEPTOR_PROPERTY_KEY, "true"
        ));

        ConnectionDto connection = new ConnectionDto();
        connection.setId(connectionId);
        connection.setName("Default");

        FullConnectionDto c = new FullConnectionDto();
        c.setAuthorization(null);
        c.setConnector(connector);
        c.setConnection(connection);

        return new ConnectorExecutorContext(invocationId, ic, c);
    }
}
