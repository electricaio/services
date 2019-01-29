package io.electrica.websocket.session;

import io.electrica.common.context.IdentityContextHolder;
import io.electrica.metric.instance.session.dto.UpsertInstanceSessionDto;
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
        UpsertInstanceSessionDto upsertInstanceSessionDto = new UpsertInstanceSessionDto(
                sdkInstanceContext.getInstanceId(),
                sdkInstanceContext.getInstanceName(),
                sdkInstanceContext.getInstanceStartClientTime()
        );

        try {
            identityContextHolder.executeWithContext(sdkInstanceContext.getIdentity(),
                    () -> instanceSessionClient.asyncStarted(upsertInstanceSessionDto));
        } catch (IOException | ServletException e) {
            log.error("Can not started session for instance id {}", sdkInstanceContext.getInstanceId(), e);
        }
    }

    public void stop(SdkInstanceContext sdkInstanceContext, CloseStatus status) {
        try {
            identityContextHolder.executeWithContext(sdkInstanceContext.getIdentity(), () -> {
                if (CloseStatus.NORMAL.equalsCode(status)) {
                    instanceSessionClient.asyncStopped(sdkInstanceContext.getInstanceId(),
                            sdkInstanceContext.getInstanceStartClientTime());
                } else {
                    instanceSessionClient.asyncExpired(sdkInstanceContext.getInstanceId(),
                            sdkInstanceContext.getInstanceStartClientTime());
                }
            });
        } catch (IOException | ServletException e) {
            log.error("Can not stopped/expired session for instance id {}", sdkInstanceContext.getInstanceId(), e);
        }
    }
}
