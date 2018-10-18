package io.electrica.user.rest;

import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static io.electrica.common.rest.PathConstants.PUBLIC;
import static io.electrica.common.rest.PathConstants.V1;

/**
 * User controller for managing users.
 */
public interface UserController {

    @PostMapping(PUBLIC + V1 + "/users")
    ResponseEntity<UserDto> createUser(@RequestBody CreateUserDto user);

    @GetMapping(V1 + "/users/{id}")
    ResponseEntity<UserDto> getUser(@PathVariable Long id);

    @PostMapping(V1 + "/organizations/{id}/users")
    ResponseEntity<List<UserDto>> getUsersForOrganization(@PathVariable Long id);

}
