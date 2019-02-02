package io.electrica.websocket.session;

import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityContextHolder;
import io.electrica.metric.instance.session.dto.InstanceSessionDescriptorDto;
import io.electrica.metric.instance.session.dto.InstanceSessionDetailedDescriptorDto;
import io.electrica.metric.instance.session.feign.InstanceSessionClient;
import io.electrica.websocket.context.SdkInstanceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;

import javax.inject.Inject;
import javax.servlet.ServletException;
import java.io.IOException;

@Slf4j
@Component
public class InstanceSessionChangeSender {
    private final InstanceSessionClient instanceSessionClient;
    private final IdentityContextHolder identityContextHolder;

    @Inject
    public InstanceSessionChangeSender(InstanceSessionClient instanceSessionClient,
                                       IdentityContextHolder identityContextHolder) {
        this.instanceSessionClient = instanceSessionClient;
        this.identityContextHolder = identityContextHolder;
    }

    public void start(SdkInstanceContext sdkInstanceContext) {
        InstanceSessionDetailedDescriptorDto instanceSessionDescriptorDto = toDetailedDescriptor(sdkInstanceContext);
        try {
            identityContextHolder.executeWithContext(sdkInstanceContext.getIdentity(),
                    () -> instanceSessionClient.asyncStarted(instanceSessionDescriptorDto));
        } catch (IOException | ServletException e) {
            log.error("Can not started session for instance id {}", sdkInstanceContext.getInstanceId(), e);
        }
    }

    public void stop(SdkInstanceContext sdkInstanceContext, CloseStatus status) {
        try {
            identityContextHolder.executeWithContext(sdkInstanceContext.getIdentity(), () -> {
                InstanceSessionDescriptorDto dto = toDescriptor(sdkInstanceContext);
                if (CloseStatus.NORMAL.equalsCode(status)) {
                    instanceSessionClient.asyncStopped(dto);
                } else {
                    instanceSessionClient.asyncExpired(dto);
                }
            });
        } catch (IOException | ServletException e) {
            log.error("Can not stopped/expired session for instance id {}", sdkInstanceContext.getInstanceId(), e);
        }
    }

    private InstanceSessionDetailedDescriptorDto toDetailedDescriptor(SdkInstanceContext sdkInstanceContext) {
        Identity identity = sdkInstanceContext.getIdentity();
        return new InstanceSessionDetailedDescriptorDto(
                sdkInstanceContext.getInstanceId(),
                sdkInstanceContext.getInstanceName(),
                sdkInstanceContext.getInstanceStartClientTime(),
                identity.getUserId(),
                identity.getOrganizationId(),
                identity.getAccessKeyId()
        );
    }

    private InstanceSessionDescriptorDto toDescriptor(SdkInstanceContext sdkInstanceContext) {
        return new InstanceSessionDescriptorDto(
                sdkInstanceContext.getInstanceId(),
                sdkInstanceContext.getInstanceName(),
                sdkInstanceContext.getInstanceStartClientTime()
        );
    }
}
