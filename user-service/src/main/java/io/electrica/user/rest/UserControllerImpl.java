package io.electrica.user.rest;

import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.service.UserDtoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * User controller implementation.
 */
@RestController
public class UserControllerImpl implements UserController {

    private final Logger logger = LoggerFactory.getLogger(UserControllerImpl.class);

    private final UserDtoService userDtoService;

    public UserControllerImpl(UserDtoService userDtoService) {
        this.userDtoService = userDtoService;
    }

    @Override
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserDto createUserDto) {
        logger.debug("REST request to save User : {}", createUserDto);
        UserDto result = userDtoService.create(createUserDto);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        UserDto userDto = userDtoService.findById(id, false);
        return ResponseEntity.ok(userDto);
    }

    @Override
    @PreAuthorize("#common.hasRole(SuperAdmin) or (#common.hasPermission(PermissionType.ReadOrg) " +
            " and (#common.hasRole(OgrUser or OrgAdmin) and #common.userInOrganization(#orgId) )")
       public ResponseEntity<List<UserDto>> getUsersForOrganization(@PathVariable Long id) {
        List<UserDto> users = userDtoService.getUsersForOrg(id);
        return ResponseEntity.ok(users);
    }

}
