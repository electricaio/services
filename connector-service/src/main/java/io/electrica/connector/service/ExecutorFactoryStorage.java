package io.electrica.connector.service;

import io.electrica.integration.spi.ConnectorExecutorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ExecutorFactoryStorage {

    private static final Logger log = LoggerFactory.getLogger(ExecutorFactoryStorage.class);

    private final Map<String, ConnectorExecutorFactory> factoriesByErn;

    public ExecutorFactoryStorage() {
        this.factoriesByErn = loadFactories();
    }

    private static Map<String, ConnectorExecutorFactory> loadFactories() {
        Map<String, ConnectorExecutorFactory> result = new HashMap<>();
        ServiceLoader<ConnectorExecutorFactory> factoryLoader = ServiceLoader.load(ConnectorExecutorFactory.class);
        factoryLoader.forEach(factory -> {
            String ern = factory.getErn().toLowerCase();
            ConnectorExecutorFactory prev = result.put(ern, factory);
            if (prev != null) {
                throw new IllegalStateException("Duplicate connector factories for ERN: " + ern);
            }
        });

        List<String> erns = result.keySet().stream().sorted().collect(Collectors.toList());
        log.info("Following executor factories has been loaded: " + erns);
        return result;
    }

    Optional<ConnectorExecutorFactory> getFactory(String ern) {
        return Optional.ofNullable(factoriesByErn.get(ern.toLowerCase()));
    }
}
