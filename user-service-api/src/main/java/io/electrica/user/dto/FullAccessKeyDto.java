package io.electrica.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FullAccessKeyDto extends AccessKeyDto {

    private UUID jti;
    private String key;

}
