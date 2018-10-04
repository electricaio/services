package io.electrica.common.filter;

import org.junit.Test;
import org.springframework.context.ApplicationContext;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ApplicationContextHeaderCustomFilterTest {

    @Test
    public void doFilterInternal() throws Exception {
        String contextId = "auth-service:dev";
        String nodeId = "123123123";
        String version = "0.0.1";

        ApplicationContext context = mock(ApplicationContext.class);
        when(context.getId()).thenReturn(contextId);

        AtomicBoolean checked = new AtomicBoolean();
        HttpServletResponse response = mock(HttpServletResponse.class);
        doAnswer(invocation -> {
            String headerName = invocation.getArgument(0);
            assertEquals(headerName, ApplicationContextHeaderCustomFilter.HEADER_NAME);

            String headerValue = invocation.getArgument(1);
            assertEquals(headerValue, version + ":" + contextId + ":" + nodeId);
            checked.set(true);
            return null;
        }).when(response).addHeader(any(String.class), any(String.class));

        ApplicationContextHeaderCustomFilter f = new ApplicationContextHeaderCustomFilter(context, version, nodeId);
        f.doFilterInternal(mock(HttpServletRequest.class), response, mock(FilterChain.class));

        assertTrue(checked.get());
    }
}
