package io.electrica.user.feign;

import io.electrica.user.rest.OrganizationController;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "organizationClient", url = "${feign.userService.url}")
public interface OrganizationClient extends OrganizationController {
}
