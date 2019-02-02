package io.electrica.metric.instance.session.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class InstanceSessionDetailedDescriptorDto extends InstanceSessionDescriptorDto {
    private Long userId;
    private Long organizationId;
    private Long accessKeyId;

    public InstanceSessionDetailedDescriptorDto(UUID id, String name, ZonedDateTime startedClientTime,
                                                Long userId, Long organizationId, Long accessKeyId) {
        super(id, name, startedClientTime);
        this.userId = userId;
        this.organizationId = organizationId;
        this.accessKeyId = accessKeyId;
    }
}
