package io.electrica.metric.common.mq.instance.session.event;

import io.electrica.metric.common.mq.MetricEvent;
import io.electrica.metric.instance.session.dto.InstanceSessionDescriptorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InstanceConnectionClosedEvent implements MetricEvent {
    private int code;
    @Delegate
    private InstanceSessionDescriptorDto descriptor;

    public static InstanceConnectionClosedEvent of(UUID id, String name, ZonedDateTime startedClientTime,
                                                   long userId, long organizationId, long accessKeyId, int closedCode) {
        InstanceSessionDescriptorDto descriptor = new InstanceSessionDescriptorDto(id, name, startedClientTime,
                userId, organizationId, accessKeyId);
        return new InstanceConnectionClosedEvent(closedCode, descriptor);
    }
}
