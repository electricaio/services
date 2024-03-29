package io.electrica.connector.service;

import io.electrica.connector.dto.ConnectorExecutorContext;
import io.electrica.connector.spi.ConnectorExecutorFactory;
import io.electrica.connector.spi.ConnectorProperties;
import io.electrica.connector.spi.impl.MapConnectorProperties;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ExecutorFactoryStorage {

    private static final Logger log = LoggerFactory.getLogger(ExecutorFactoryStorage.class);

    private final Map<String, ConnectorExecutorFactory> loaded = new HashMap<>();
    private final ConcurrentMap<String, ConnectorExecutorFactory> factories = new ConcurrentHashMap<>();

    @PostConstruct
    public void loadFactories() {
        ServiceLoader<ConnectorExecutorFactory> factoryLoader = ServiceLoader.load(ConnectorExecutorFactory.class);
        factoryLoader.forEach(factory -> {
            String ern = factory.getErn().toLowerCase();
            try {
                factory.afterLoad();
                ConnectorExecutorFactory prev = loaded.put(ern, factory);
                if (prev != null) {
                    log.error(
                            "Connector factory {} skipped due to duplicate ern: {}",
                            factory.getClass().getName(), ern
                    );
                }
            } catch (Exception e) {
                log.error("Connector skipped due to 'afterLoad' error: " + ern, e);
            }
        });

        List<String> loadedErns = loaded.keySet()
                .stream()
                .sorted()
                .collect(Collectors.toList());

        log.info("Following executor factories has been loaded: " + loadedErns);
    }

    @PreDestroy
    public void tearDown() {
        loaded.values().forEach(factory -> {
            try {
                factory.beforeDestroy();
            } catch (Exception e) {
                log.error("Error in 'beforeDestroy' method", e);
            }
        });
    }

    ConnectorExecutorFactory getFactory(ConnectorExecutorContext context) {
        String ern = context.getConnection().getConnector().getErn().toLowerCase();
        return factories.computeIfAbsent(ern, new Function<String, ConnectorExecutorFactory>() {
            @Override
            @SneakyThrows
            public ConnectorExecutorFactory apply(String ern) {
                ConnectorExecutorFactory factory = loaded.get(ern);
                if (factory == null) {
                    throw new IllegalStateException("Connector executor not found: " + ern);
                }
                Map<String, String> properties = context.getConnection().getConnector().getProperties();
                ConnectorProperties connectorProperties = properties == null ?
                        ConnectorProperties.EMPTY :
                        new MapConnectorProperties(properties);
                factory.setup(connectorProperties);
                return factory;
            }
        });
    }

}
