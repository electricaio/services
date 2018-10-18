package io.electrica.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessKeyDto {

    private Long id;
    private String name;
    private String key;
    private Long revisionVersion;
    private Long userId;

}
