package io.electrica.stl.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.electrica.stl.repository.AbstractDatabaseTest;
import io.electrica.stl.util.Fixture;
import io.electrica.stl.util.TestHelpers;
import org.junit.Before;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;

public abstract class AbstractApiTest extends AbstractDatabaseTest implements TestHelpers {

    protected MockMvc mockMvc;

    @Inject
    public ObjectMapper objectMapper;

    @Inject
    private WebApplicationContext context;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
