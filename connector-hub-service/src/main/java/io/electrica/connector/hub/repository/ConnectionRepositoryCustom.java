package io.electrica.connector.hub.repository;

import io.electrica.connector.hub.model.Connection;
import java.util.List;

public interface ConnectionRepositoryCustom {

    List<Connection> findAllWithSearch(Long accessKeyId, String connectionName);
}

