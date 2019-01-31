package io.electrica.metric.instance.session.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
public class InstanceSessionDto {
    private UUID id;
    private Long userId;
    private Long organizationId;
    private Long accessKeyId;
    private String name;
    private String sessionState;
    private ZonedDateTime startedClientTime;
    private LocalDateTime startedTime;
    private LocalDateTime stoppedTime;
    private LocalDateTime expiredTime;
}
