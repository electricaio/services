package io.electrica.user.service;

import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.model.Organization;
import io.electrica.user.model.User;
import io.electrica.user.service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collections;

@Component
public class UserDtoService {

    private static final Logger log = LoggerFactory.getLogger(UserDtoService.class);

    private final UserServiceImpl userService;

    @Inject
    public UserDtoService(UserServiceImpl userService) {
        this.userService = userService;
    }

    public UserDto createUser(CreateUserDto user) {
        User newUser = userService.create(toEntity(user));
        UserDto userDto = toDto(newUser);
        return userDto;
    }

    private User toEntity(UserDto userDto) {
        User user = new User();
        user.setUuid(userDto.getUuid());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setOrganization(toEntity(userDto.getOrganization()));
        if (userDto instanceof CreateUserDto) {
            user.setSaltedPassword(((CreateUserDto) userDto).getPassword());
        }
        return user;
    }

    public UserDto toDto(User e) {
        return new UserDto(
                e.getId(),
                e.getUuid(),
                e.getFirstName(),
                e.getLastName(),
                e.getEmail(),
                null,
                Collections.EMPTY_SET,
                Collections.EMPTY_SET,
                null,
                null
        );
    }

    private Organization toEntity(OrganizationDto organizationDto){
        Organization org = new Organization();
        org.setId(organizationDto.getId());
        return org;
    }


}
