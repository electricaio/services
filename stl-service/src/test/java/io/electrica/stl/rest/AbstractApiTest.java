package io.electrica.stl.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.electrica.stl.repository.AbstractDatabaseTest;
import io.electrica.stl.util.TestApiHelper;
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
