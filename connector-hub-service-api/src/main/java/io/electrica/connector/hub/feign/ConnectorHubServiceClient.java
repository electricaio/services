package io.electrica.connector.hub.feign;

import io.electrica.connector.hub.rest.ConnectorController;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "ConnectorClient", url = "${feign.connectorHubService.client.url}")
public interface ConnectorHubServiceClient extends ConnectorController {
}
