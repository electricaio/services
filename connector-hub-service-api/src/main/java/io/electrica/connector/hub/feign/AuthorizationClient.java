package io.electrica.connector.hub.feign;

import io.electrica.connector.hub.rest.AuthorizationController;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "authorizationClient", url = "${feign.connectorHubService.url}")
public interface AuthorizationClient extends AuthorizationController {
}
