package io.electrica.user.service;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    List<RoleDto> findAll();
    Optional<RoleDto> findById(Long roleId);
}
