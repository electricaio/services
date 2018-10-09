package io.electrica.user.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDto {

    private Long id;
    private String name;
    private String description;

    public RoleDto(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
