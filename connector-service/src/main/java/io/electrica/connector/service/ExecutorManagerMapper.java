package io.electrica.connector.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.electrica.connector.dto.ConnectorExecutorContext;
import io.electrica.connector.dto.ConnectorExecutorResult;
import io.electrica.connector.dto.ErrorDto;
import io.electrica.connector.dto.InvocationContext;
import io.electrica.connector.hub.dto.BasicAuthorizationDto;
import io.electrica.connector.hub.dto.TokenAuthorizationDto;
import io.electrica.connector.hub.dto.sdk.BasicTypedAuthorizationDto;
import io.electrica.connector.hub.dto.sdk.FullConnectionDto;
import io.electrica.connector.hub.dto.sdk.TokenTypedAuthorizationDto;
import io.electrica.connector.hub.dto.sdk.TypedAuthorizationDto;
import io.electrica.integration.spi.context.Authorization;
import io.electrica.integration.spi.context.BasicAuthorization;
import io.electrica.integration.spi.context.ExecutionContext;
import io.electrica.integration.spi.context.TokenAuthorization;
import io.electrica.integration.spi.exception.ExceptionCodes;
import io.electrica.integration.spi.exception.IntegrationException;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@Component
public class ExecutorManagerMapper {

    private final ObjectMapper objectMapper;

    @Inject
    public ExecutorManagerMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private static Authorization toSdkAuthorization(TypedAuthorizationDto dto) {
        if (dto instanceof BasicTypedAuthorizationDto) {
            BasicAuthorizationDto data = ((BasicTypedAuthorizationDto) dto).getData();
            return new BasicAuthorization(data.getUsername(), data.getPassword());
        } else if (dto instanceof TokenTypedAuthorizationDto) {
            TokenAuthorizationDto data = ((TokenTypedAuthorizationDto) dto).getData();
            return new TokenAuthorization(data.getToken());
        }
        return null;
    }

    ExecutionContext toSdkContext(ConnectorExecutorContext c) {
        InvocationContext context = c.getInvocationContext();
        FullConnectionDto fullConnection = c.getConnection();
        return new ExecutionContext(
                c.getInvocationId(),
                context.getInstanceId(),
                fullConnection.getConnection().getName(),
                fullConnection.getConnection().getTenantRefId(),
                toSdkAuthorization(fullConnection.getAuthorization()),
                context.getParameters(),
                context.getPayload()
        );
    }

    ConnectorExecutorResult toResult(ConnectorExecutorContext c, @Nullable Object result) {
        return new ConnectorExecutorResult(
                c.getInvocationId(),
                c.getInvocationContext().getInstanceId(),
                c.getConnection().getConnection().getId(),
                true,
                result == null ? null : objectMapper.convertValue(result, JsonNode.class),
                null
        );
    }

    ConnectorExecutorResult toErrorResult(ConnectorExecutorContext c, Exception e) {
        return new ConnectorExecutorResult(
                c.getInvocationId(),
                c.getInvocationContext().getInstanceId(),
                c.getConnection().getConnection().getId(),
                false,
                null,
                toErrorDto(e)
        );
    }

    private ErrorDto toErrorDto(Exception e) {
        String code;
        List<String> payload;
        if (e instanceof IntegrationException) {
            IntegrationException ie = (IntegrationException) e;
            code = ie.getCode();
            payload = ie.getPayload();
        } else {
            code = ExceptionCodes.GENERIC;
            payload = null;
        }
        return new ErrorDto(code, e.getMessage(), getStackTrace(e), payload);
    }

    private String getStackTrace(Exception e) {
        StringWriter stackTraceWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTraceWriter));
        return stackTraceWriter.toString();
    }
}
