package io.electrica.connector.service.facade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import io.electrica.connector.dto.ConnectorExecutorContext;
import io.electrica.integration.spi.ConnectorExecutorFactory;
import io.electrica.integration.spi.ServiceFacade;
import io.electrica.integration.spi.service.Logger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import okhttp3.OkHttpClient;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ServiceFacadeFactory {

    private final ObjectMapper objectMapper;
    private final ConcurrentMap<Long, ConnectorContext> connectorContexts = new ConcurrentHashMap<>();

    @Inject
    public ServiceFacadeFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private static Logger createLogger(ConnectorExecutorContext context) {
        String ern = context.getConnection().getConnector().getErn();
        org.slf4j.Logger logger = LoggerFactory.getLogger(ern);
        return new LoggerImpl(logger);
    }

    public ServiceFacade create(ConnectorExecutorContext context, ConnectorExecutorFactory executorFactory) {
        Long connectorId = context.getConnection().getConnector().getId();
        ConnectorContext connectorContext = connectorContexts.computeIfAbsent(connectorId, id -> {
            ConnectorContext.Builder contextBuilder = new ConnectorContext.Builder();
            executorFactory.configureServices(contextBuilder);
            return contextBuilder.build();
        });

        return new ServiceFacadeImpl(
                createLogger(context),
                objectMapper.reader(),
                connectorContext.getHttpClient()
        );
    }

    @Getter
    @AllArgsConstructor
    private static class ServiceFacadeImpl implements ServiceFacade {
        private final Logger logger;
        private final ObjectReader objectReader;
        private final OkHttpClient httpClient;
    }

}
