package io.electrica.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccessKeyDto {

    private String name;
    private Long userId;

}
