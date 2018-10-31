package io.electrica.user.feign;


import io.electrica.user.rest.UserController;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(name = "userClient", url = "${feign.userService.url}")
public interface UserClient extends UserController {
}
