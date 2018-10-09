package io.electrica.user.rest;

import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URISyntaxException;

import static io.electrica.common.rest.PathConstants.PUBLIC;
import static io.electrica.common.rest.PathConstants.V1;

/**
 *  REST Client for Managing users.
 *
 */
public interface UserRestClient {

    @PostMapping(PUBLIC + V1 + "/users")
    ResponseEntity<UserDto> createUser(@RequestBody CreateUserDto user) throws URISyntaxException;

   /* @PutMapping(V1 + "/users/{id}")
    ResponseEntity<UserDto> updateUser(@PathVariable int id, @RequestBody UserDto user);

    @GetMapping(V1 + "/users/me")
    ResponseEntity<UserDto> getMe();*/

}
