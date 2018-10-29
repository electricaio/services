package io.electrica.user.feign;


import io.electrica.user.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static io.electrica.common.rest.PathConstants.V1;


@FeignClient(name = "userServiceClient", url = "http://localhost:22022")
public interface UserServiceClient {

    @RequestMapping(value = V1 + "/users/{id}", method = RequestMethod.GET)
    ResponseEntity<UserDto> getUser(@PathVariable("id") Long id);
}
