package io.electrica.user.rest;

import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
/**
 *  REST Client implementation for Managing users..
 *
 *  @author Munish Sodhi
 */
@RestController
public class UserRestClientImpl implements UserRestClient {

    @Override
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserDto user) {
        // ToDo stubbed
        return ResponseEntity.ok(new UserDto());
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
