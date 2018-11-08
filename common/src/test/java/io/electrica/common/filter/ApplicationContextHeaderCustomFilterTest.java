package io.electrica.common.filter;

import io.electrica.common.EnvironmentType;
import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.electrica.common.filter.ApplicationContextHeaderCustomFilter.buildHeaderValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class ApplicationContextHeaderCustomFilterTest {

    @Test
    public void doFilterInternal() throws Exception {
        String applicationName = "auth-service";
        String nodeId = "817d88b8-eb38-4da7-839f-930d83d99e6f";
        EnvironmentType environmentType = EnvironmentType.Development;
        String version = "0.0.1";

        AtomicBoolean checked = new AtomicBoolean();
        HttpServletResponse response = mock(HttpServletResponse.class);
        doAnswer(invocation -> {
            String headerName = invocation.getArgument(0);
            assertEquals(headerName, ApplicationContextHeaderCustomFilter.HEADER_NAME);

            String headerValue = invocation.getArgument(1);
            String expectedValue = buildHeaderValue(environmentType, version, nodeId, applicationName);
            assertEquals(expectedValue, headerValue);
            checked.set(true);
            return null;
        }).when(response).addHeader(any(String.class), any(String.class));

        ApplicationContextHeaderCustomFilter f =
                new ApplicationContextHeaderCustomFilter(environmentType, version, nodeId, applicationName);
        f.doFilterInternal(mock(HttpServletRequest.class), response, mock(FilterChain.class));

        assertTrue(checked.get());
    }
}
