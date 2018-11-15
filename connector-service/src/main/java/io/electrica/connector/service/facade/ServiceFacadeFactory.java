package io.electrica.connector.service.facade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import io.electrica.connector.dto.ConnectorExecutorContext;
import io.electrica.integration.spi.ServiceFacade;
import io.electrica.integration.spi.context.ExecutionContext;
import io.electrica.integration.spi.service.Logger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class ServiceFacadeFactory {

    private final ObjectMapper objectMapper;

    @Inject
    public ServiceFacadeFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private static Logger createLogger(ConnectorExecutorContext context) {
        String ern = context.getConnection().getConnector().getErn();
        org.slf4j.Logger logger = LoggerFactory.getLogger(ern);
        return new LoggerImpl(logger);
    }

    public ServiceFacade create(ConnectorExecutorContext context, ExecutionContext sdkContext) {
        return new ServiceFacadeImpl(
                createLogger(context),
                sdkContext,
                objectMapper.reader()
        );
    }

    @Getter
    @AllArgsConstructor
    private static class ServiceFacadeImpl implements ServiceFacade {
        private final Logger logger;
        private final ExecutionContext context;
        private final ObjectReader objectReader;
    }

}
