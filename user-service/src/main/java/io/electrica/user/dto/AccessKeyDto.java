package io.electrica.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessKeyDto extends CreateAccessKeyDto {

    private Long id;
    private Long revisionVersion;

}
