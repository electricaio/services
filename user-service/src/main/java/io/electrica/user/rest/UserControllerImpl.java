package io.electrica.user.rest;

import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.service.UserDtoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
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
    @PreAuthorize(" #common.hasPermission('ReadUser')  " +
            " and  (   " +
            "  #common.isSuperAdmin()  OR  #common.haveOneOfRoles('OrgUser' , 'OrgAdmin') " +
            "   ) ")
    @PostAuthorize(" ( #common.isOrgAdmin() AND #common.userInOrganization(" +
            "          returnObject.getBody().getOrganizationId()) " +
            "        ) OR    (  #common.isOrgUser() AND #common.isUser(#id) )   " +
            "          OR #common.isSuperAdmin() ")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        UserDto userDto = userDtoService.findById(id, false);
        return ResponseEntity.ok(userDto);
    }

    @Override
    @PreAuthorize(" #common.hasPermission('ReadOrg')  and " +
            " ( #common.isSuperAdmin() OR    (#common.haveOneOfRoles('OrgUser' , 'OrgAdmin') and  " +
            "#common.userInOrganization(#organizationId)) )")
    public ResponseEntity<List<UserDto>> getUsersForOrganization(@PathVariable Long organizationId) {
        List<UserDto> users = userDtoService.getUsersForOrg(organizationId);
        return ResponseEntity.ok(users);
    }

}
