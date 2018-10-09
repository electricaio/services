package io.electrica.user.service.impl;

import io.electrica.user.dto.RoleDto;
import io.electrica.user.model.Role;
import io.electrica.user.repository.RoleRepository;
import io.electrica.user.service.RoleService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository rolerepository) {
        this.roleRepository = rolerepository;
    }

    @Override
    public List<RoleDto> findAll() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(r -> toDto(r))
                .collect(Collectors.toList());

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

    public RoleDto toDto(Role role) {
        return new RoleDto(
            role.getId(),role.getName(),role.getDescription()
        );
    }
}
