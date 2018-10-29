package io.electrica.connector.hub.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "connectorHubServiceClient", url = "http://localhost:22023")
public interface ConnectorHubServiceClient {

}
