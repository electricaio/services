package io.electrica.common.filter;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter implements Ordered {
    private static final int MAX_BODY_LENGTH = 10000;
    private static final Set<String> EXCLUDED_HEADERS = ImmutableSet.of(HttpHeaders.AUTHORIZATION);

    private int order = Ordered.LOWEST_PRECEDENCE - 10;

    private final IdentityContextHolder identityContextHolder;

    @Inject
    public RequestResponseLoggingFilter(IdentityContextHolder identityContextHolder) {
        this.identityContextHolder = identityContextHolder;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (log.isDebugEnabled() && !request.getRequestURI().contains("invoke")) {
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
            ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
            Identity identity = MoreObjects.firstNonNull(identityContextHolder.getIdentity(), Identity.ANONYMOUS);
            try {
                filterChain.doFilter(wrappedRequest, wrappedResponse);
            } finally {
                identityContextHolder.executeWithContext(identity, () -> {
                    log.debug("Request {}", createRequestMessage(wrappedRequest));
                    log.debug("Response {}", createResponseMessage(wrappedResponse));
                });
                wrappedResponse.copyBodyToResponse();
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private String createRequestMessage(ContentCachingRequestWrapper request) {
        StringBuilder msg = new StringBuilder();
        msg.append("uri=").append(request.getRequestURI());

        String queryString = request.getQueryString();
        if (queryString != null) {
            msg.append('?').append(queryString);
        }

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

        Enumeration<String> headerNames = MoreObjects.firstNonNull(
                request.getHeaderNames(),
                Collections.emptyEnumeration()
        );
        Map<String, Collection<String>> headers = extractHeaders(Collections.list(headerNames),
                h -> Collections.list(request.getHeaders(h)));
        msg.append(";headers=").append(headers);
        String payload = cutPayload(request.getContentAsByteArray(), request.getCharacterEncoding());
        if (payload != null) {
            msg.append(";payload=").append(payload);
        }
        return msg.toString();
    }

    private String createResponseMessage(ContentCachingResponseWrapper response) throws IOException {
        StringBuilder msg = new StringBuilder();
        msg.append("status=").append(response.getStatus());
        msg.append(";headers=").append(extractHeaders(response.getHeaderNames(), response::getHeaders));
        String payload = cutPayload(response.getContentAsByteArray(), response.getCharacterEncoding());
        if (payload != null) {
            msg.append(";payload=").append(payload);
        }
        return msg.toString();
    }

    private String cutPayload(byte[] payload, String characterEncoding) {
        if (payload.length > 0) {
            int length = Math.min(payload.length, MAX_BODY_LENGTH);
            try {
                return new String(payload, 0, length, characterEncoding);
            } catch (UnsupportedEncodingException ex) {
                return "[unknown]";
            }
        }
        return null;
    }

    private Map<String, Collection<String>> extractHeaders(Collection<String> headers,
                                                           Function<String, Collection<String>> headerValues) {
        return headers.stream()
                .filter(header -> !EXCLUDED_HEADERS.contains(header))
                .collect(Collectors.toMap(h -> h, headerValues));
    }
}
