package io.electrica.connector.service;

import io.electrica.connector.dto.ConnectorExecutorContext;
import io.electrica.connector.dto.ConnectorExecutorResult;
import io.electrica.connector.service.facade.ServiceFacadeFactory;
import io.electrica.integration.spi.ConnectorExecutor;
import io.electrica.integration.spi.ConnectorExecutorFactory;
import io.electrica.integration.spi.ServiceFacade;
import io.electrica.integration.spi.context.ExecutionContext;
import io.electrica.integration.spi.exception.IntegrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class ExecutorManager {

    private static final Logger log = LoggerFactory.getLogger(ExecutorManager.class);

    private final ServiceFacadeFactory serviceFacadeFactory;
    private final ExecutorFactoryStorage executorFactoryStorage;
    private final ExecutorManagerMapper executorManagerMapper;

    @Inject
    public ExecutorManager(
            ServiceFacadeFactory serviceFacadeFactory,
            ExecutorFactoryStorage executorFactoryStorage,
            ExecutorManagerMapper executorManagerMapper
    ) {
        this.serviceFacadeFactory = serviceFacadeFactory;
        this.executorFactoryStorage = executorFactoryStorage;
        this.executorManagerMapper = executorManagerMapper;
    }

    public ConnectorExecutorResult execute(ConnectorExecutorContext context) {
        try {
            ConnectorExecutorFactory executorFactory = executorFactoryStorage.getFactory(context);
            ExecutionContext sdkContext = executorManagerMapper.toSdkContext(context);
            ServiceFacade serviceFacade = serviceFacadeFactory.create(context, sdkContext);
            ConnectorExecutor executor = executorFactory.create(serviceFacade);
            return executorManagerMapper.toResult(context, executor.run());
        } catch (Exception e) {
            boolean integrationException = e instanceof IntegrationException;
            if (!integrationException) {
                log.error("Execution failed for context: " + context, e);
            }
            return executorManagerMapper.toErrorResult(context, e);
        }
    }
}
