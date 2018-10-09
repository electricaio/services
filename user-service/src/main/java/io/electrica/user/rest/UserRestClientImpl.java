package io.electrica.user.rest;

import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.model.User;
import io.electrica.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * REST Client implementation for Managing users..
 */
@RestController
public class UserRestClientImpl implements UserRestClient {

    private final Logger logger = LoggerFactory.getLogger(UserRestClientImpl.class);

    UserService userService;

    public UserRestClientImpl(UserService userService) {
        this.userService = userService;
    }


    @Override
    public ResponseEntity<User> createUser(@RequestBody CreateUserDto createUserDto) throws URISyntaxException {
        logger.debug("REST request to save User : {}", createUserDto);
        if (userService.findOneByLogin(createUserDto.getEmail()).isPresent()) {
            return new ResponseEntity("Login already in use", HttpStatus.CONFLICT);
        } else {
            User newUser = userService.createUser(createUserDto);
            return ResponseEntity.created(new URI("/api/users/" + newUser.getEmail()))
                    .body(newUser);
        }
    }





   /* @Override
    @PreAuthorize("#oauth2.hasScope('update') and #common.isUser(#id) and #id == #user.id")
    public ResponseEntity<UserDto> updateUser(@PathVariable int id, @RequestBody UserDto user) {
        // ToDo stubbed
        return ResponseEntity.ok(new UserDto());
    }

    @Override
    @PreAuthorize("#oauth2.hasScope('get')")
    public ResponseEntity<UserDto> getMe() {
        // ToDo stubbed
        return ResponseEntity.ok(new UserDto());
    }*/
}
