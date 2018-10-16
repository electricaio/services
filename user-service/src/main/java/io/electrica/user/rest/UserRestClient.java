package io.electrica.user.rest;

import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static io.electrica.common.rest.PathConstants.PUBLIC;
import static io.electrica.common.rest.PathConstants.V1;

/**
 *  REST Client for Managing users.
 *
 */
public interface UserRestClient {

    @PostMapping(PUBLIC + V1 + "/users")
    ResponseEntity<UserDto> createUser(@RequestBody CreateUserDto user);

}
