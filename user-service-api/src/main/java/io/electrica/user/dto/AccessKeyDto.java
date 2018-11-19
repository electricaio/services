package io.electrica.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AccessKeyDto extends CreateAccessKeyDto {

    private Long id;
    private Long revisionVersion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
