package io.electrica.user.rest;

import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.service.UserDtoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Client implementation for Managing users..
 */
@RestController
public class UserRestClientImpl implements UserRestClient {

    private final Logger logger = LoggerFactory.getLogger(UserRestClientImpl.class);

    private final UserDtoService userDtoService;

    public UserRestClientImpl(UserDtoService userDtoService) {
        this.userDtoService = userDtoService;
    }

    @Override
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserDto createUserDto) {
        logger.debug("REST request to save User : {}", createUserDto);
        UserDto result = userDtoService.create(createUserDto);
        return ResponseEntity.ok(result);
    }

}
