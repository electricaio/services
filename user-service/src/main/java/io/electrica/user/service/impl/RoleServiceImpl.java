package io.electrica.user.service.impl;

import io.electrica.user.model.Role;
import io.electrica.user.repository.RoleRepository;
import io.electrica.user.repository.UserRoleRepository;
import io.electrica.user.service.RoleService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public RoleServiceImpl(RoleRepository roleRepository, UserRoleRepository userRoleRepository) {
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public List<RoleDto> findAll() {
        return roleRepository.findAll()
                .stream()
                .map(r -> toDto(r))
                .collect(Collectors.toList());

    }

    public List<Role> findRolesForUser(Long userId) {
        return userRoleRepository.findById(userId)
                .map(r -> toDto(r));
    }

    @Override
    public Optional<RoleDto> findById(Long roleId) {
        return roleRepository.findById(roleId)
                .map(role -> toDto(role));
    }

    public Role toEntity(RoleDto roleDto) {
        Role role = new Role();
        role.setName(roleDto.getName());
        role.setDescription(roleDto.getDescription());
        return role;
    }

    public Role toEntity(RoleDto roleDto) {
        Role role = new Role();
        role.setName(roleDto.getName());
        role.setDescription(roleDto.getDescription());
        return role;
    }

    public RoleDto toDto(Role role) {
        return new RoleDto(
                role.getId(), role.getName(), role.getDescription()
        );
    }
}
