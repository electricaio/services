package io.electrica.user.feign;

import io.electrica.user.rest.AccessKeyController;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "accessKeyServiceClient", url = "${feign.userservice.client.url}")
public interface AccessKeyClient extends AccessKeyController {
}
