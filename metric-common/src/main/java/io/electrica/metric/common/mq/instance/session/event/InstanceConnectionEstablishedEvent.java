package io.electrica.metric.common.mq.instance.session.event;

import io.electrica.metric.common.mq.MetricEvent;
import io.electrica.metric.instance.session.dto.InstanceSessionDescriptorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InstanceConnectionEstablishedEvent implements MetricEvent {
    private InstanceSessionDescriptorDto descriptor;

    public static InstanceConnectionEstablishedEvent of(UUID id, String name, ZonedDateTime startedClientTime,
                                                        long userId, long organizationId, long accessKeyId) {
        InstanceSessionDescriptorDto descriptor = new InstanceSessionDescriptorDto(
                id, name, startedClientTime, userId, organizationId, accessKeyId);
        return new InstanceConnectionEstablishedEvent(descriptor);
    }
}
