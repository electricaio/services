package io.electrica.user.dto;

import com.github.dozermapper.core.Mapping;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessKeyDto {

    private Long id;
    private String keyName;
    private String accessKey;
    private Long revisionVersion;
    @Mapping("user.id")
    private Long userId;

}
