package io.electrica.user.rest;

import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.electrica.common.rest.PathConstants.PUBLIC;
import static io.electrica.common.rest.PathConstants.V1;

/**
 *  REST Client for Managing users.
 *
 */
public interface UserRestClient {

    @PostMapping(PUBLIC + V1 + "/users")
    ResponseEntity<UserDto> createUser(@RequestBody CreateUserDto user);

   /* @PutMapping(V1 + "/users/{id}")
    ResponseEntity<UserDto> updateUser(@PathVariable int id, @RequestBody UserDto user);

    @GetMapping(V1 + "/users/me")
    ResponseEntity<UserDto> getMe();*/

}
