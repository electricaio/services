package io.electrica.invoker.service;

import com.github.dozermapper.core.Mapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.sdk.FullConnectionDto;
import io.electrica.connector.hub.feign.ConnectionClient;
import io.electrica.invoker.dto.InvocationContext;
import io.electrica.invoker.dto.TinyConnectionDto;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InvokerService {

    private final ConnectionClient connectionClient;
    private final Mapper mapper;

    @Inject
    public InvokerService(ConnectionClient connectionClient, Mapper mapper) {
        this.connectionClient = connectionClient;
        this.mapper = mapper;
    }

    public Object invokeSync(InvocationContext context) {
        FullConnectionDto connection = connectionClient.getFull(context.getConnectionId()).getBody();
        // TODO invoke connector service
        return connection;
    }

    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "Find a better way")
    public List<TinyConnectionDto> getConnection(String connectionName, String connector) {
        return toDto(connectionClient.findAllByAccessKey(connectionName, connector).getBody());
    }

    private List<TinyConnectionDto> toDto(List<ConnectionDto> fromList) {
        return fromList
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private TinyConnectionDto toDto(ConnectionDto entity) {
        return mapper.map(entity, TinyConnectionDto.class);
    }
}
