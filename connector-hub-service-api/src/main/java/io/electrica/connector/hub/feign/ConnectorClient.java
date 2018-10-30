package io.electrica.connector.hub.feign;

import io.electrica.connector.hub.rest.ConnectorController;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "connectorClient", url = "${feign.connectorHubService.url}")
public interface ConnectorClient extends ConnectorController {
}
