package io.electrica.user.service.impl;

import com.google.common.collect.Sets;
import io.electrica.user.dto.CreateUserDto;
import io.electrica.user.dto.UserDto;
import io.electrica.user.model.User;
import io.electrica.user.service.UserService;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation class for managing users.
 */
public class UserServiceImpl implements UserService {

    @Override
    public Optional<User> findOneByLogin(String login) {
        return Optional.empty();
    }

    @Override
    public User createUser(CreateUserDto createUserDto) {
        return null;
    }

    @Override
    public User updateUser(UserDto userDto){

    }

    public UserDto toDto(User e) {
        return new UserDto(
                e.getId(),
                e.getFirstName(),
                e.getMiddleName(),
                e.getLastName(),
                e.getEmail(),
                e.getPhone(),
                buildRolesAndAuthorities(e)
        );
    }

    public Set<String> buildAuthorities(User e) {
        return Sets.newHashSet(
                Sets.union(
                        e.getUserRoles().stream()
                                .map(userRole -> userRole.getRole().getName())
                                .collect(Collectors.toSet()),

                        e.getUserCooperativeRoles().stream()
                                .map(userCooperativeRole -> {
                                    CooperativeRole cr = userCooperativeRole.getCooperativeRole();
                                    return cr.getRole().getName() + "_" + cr.getCooperative().getRemoteId();
                                }).collect(Collectors.toSet())
                )
        );
    }

    public  User toEntity(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        if (userDto instanceof CreateUserDto) {
            user.setPassword(((CreateUserDto) userDto).getPassword());
        }

        Set<String> authorities = userDto.getAuthorities();
        if (authorities != null) {
            for (String authority : authorities) {
                Role role = new Role();
                role.setName(roleType.getName());

                UserRole userRole = new UserRole();
                userRole.setRole(role);

                e.getUserRoles().add(userRole);
                }
            }
        }
        return e;
    }
}
