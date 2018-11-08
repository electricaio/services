package io.electrica.common.filter;

import com.google.common.annotations.VisibleForTesting;
import io.electrica.common.EnvironmentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter that returns {@link ApplicationContextHeaderCustomFilter#HEADER_NAME} header with service unique identifier,
 * service name, environment and version on each request.
 */
@Component
public class ApplicationContextHeaderCustomFilter extends OncePerRequestFilter {

    @VisibleForTesting
    static final String HEADER_NAME = "X-Application-Context";

    private final String headerValue;

    @Inject
    public ApplicationContextHeaderCustomFilter(
            EnvironmentType environmentType,
            @Value("${common.service.version}") String version,
            @Value("${common.service.id}") String nodeId,
            @Value("${spring.application.name}") String applicationName
    ) {
        headerValue = buildHeaderValue(environmentType, version, nodeId, applicationName);
    }

    @VisibleForTesting
    static String buildHeaderValue(
            EnvironmentType environmentType,
            String version,
            String nodeId,
            String applicationName
    ) {
        return String.format("%s:%s:%s:%s", environmentType, applicationName, version, nodeId);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        response.addHeader(HEADER_NAME, headerValue);
        filterChain.doFilter(request, response);
    }
}
