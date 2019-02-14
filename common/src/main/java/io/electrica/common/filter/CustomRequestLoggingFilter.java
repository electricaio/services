package io.electrica.common.filter;

import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CustomRequestLoggingFilter extends CommonsRequestLoggingFilter {
    private static final Set<String> EXCLUDED_HEADERS = ImmutableSet.of(
            HttpHeaders.AUTHORIZATION
    );

    private final boolean enabled;

    @Inject
    public CustomRequestLoggingFilter(@Value("${common.request.metric.logging.enabled}") boolean enabled) {
        this.enabled = enabled;
        setup();
    }

    private void setup() {
        setIncludeHeaders(true);
        setIncludeClientInfo(true);
        setIncludeQueryString(true);
        setIncludePayload(true);
    }

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return enabled;
    }

    @Override
    protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
        StringBuilder msg = new StringBuilder();
        msg.append(prefix);
        msg.append("uri=").append(request.getRequestURI());

        if (isIncludeQueryString()) {
            String queryString = request.getQueryString();
            if (queryString != null) {
                msg.append('?').append(queryString);
            }
        }

        if (isIncludeClientInfo()) {
            String client = request.getRemoteAddr();
            if (StringUtils.hasLength(client)) {
                msg.append(";client=").append(client);
            }
            HttpSession session = request.getSession(false);
            if (session != null) {
                msg.append(";session=").append(session.getId());
            }
            String user = request.getRemoteUser();
            if (user != null) {
                msg.append(";user=").append(user);
            }
        }

        if (isIncludeHeaders()) {
            msg.append(";headers=").append(extractHeaders(request));
        }

        if (isIncludePayload()) {
            String payload = getMessagePayload(request);
            if (payload != null) {
                msg.append(";payload=").append(payload);
            }
        }

        msg.append(suffix);
        return msg.toString();
    }

    private Map<String, Collection<String>> extractHeaders(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeaderNames())
                .map(Collections::list)
                .map(headers -> headers.stream()
                        .filter(h -> !EXCLUDED_HEADERS.contains(h))
                        .collect(Collectors.toMap(
                                h -> h,
                                h -> (Collection<String>) Collections.list(request.getHeaders(h))
                        )))
                .orElse(Collections.emptyMap());
    }
}
