package io.electrica.user.feign;

import io.electrica.user.rest.AccessKeyController;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "accessKeyClient", url = "${feign.userService.url}")
public interface AccessKeyClient extends AccessKeyController {
}
