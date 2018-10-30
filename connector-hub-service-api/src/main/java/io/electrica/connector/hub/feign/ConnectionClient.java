package io.electrica.connector.hub.feign;

import io.electrica.connector.hub.rest.ConnectionController;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "connectionClient", url = "${feign.connectorHubService.url}")
public interface ConnectionClient extends ConnectionController {
}
