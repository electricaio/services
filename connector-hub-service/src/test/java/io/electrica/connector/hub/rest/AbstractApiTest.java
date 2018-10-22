package io.electrica.connector.hub.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.electrica.connector.hub.util.TestApiHelper;
import io.electrica.connector.hub.repository.AbstractDatabaseTest;
import org.junit.Before;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;

public abstract class AbstractApiTest extends AbstractDatabaseTest implements TestApiHelper {

    protected MockMvc mockMvc;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private WebApplicationContext context;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
