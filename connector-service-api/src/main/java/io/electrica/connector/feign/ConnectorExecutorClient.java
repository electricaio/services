package io.electrica.connector.feign;

import io.electrica.connector.rest.ConnectorExecutorController;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "connectorExecutorClient", url = "${feign.connectorService.url}")
public interface ConnectorExecutorClient extends ConnectorExecutorController {
}
