package io.electrica.metric.instance.session.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InstanceSessionDescriptorDto {
    private UUID id;
    private String name;
    private ZonedDateTime lastSessionStarted;
    private Long userId;
    private Long organizationId;
    private Long accessKeyId;
}
