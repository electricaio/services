package io.electrica.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FullAccessKeyDto extends AccessKeyDto {

    private String jti;
    private String key;

}
