package io.electrica.metric.security;

import io.electrica.common.security.CommonExpressionMethods;
import io.electrica.common.security.ExpressionMethodsFactory;
import io.electrica.connector.hub.feign.ConnectionClient;
import io.electrica.metric.instance.session.service.InstanceSessionService;
import io.electrica.user.feign.AccessKeyClient;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static io.electrica.common.helper.CollectionUtils.nullToFalse;

public class MetricExpressionMethods extends CommonExpressionMethods {
    private final InstanceSessionService instanceSessionService;
    private final AccessKeyClient accessKeyClient;
    private final ConnectionClient connectionClient;

    public MetricExpressionMethods(Authentication authentication, InstanceSessionService instanceSessionService,
                                   AccessKeyClient accessKeyClient, ConnectionClient connectionClient) {
        super(authentication);
        this.instanceSessionService = instanceSessionService;
        this.accessKeyClient = accessKeyClient;
        this.connectionClient = connectionClient;
    }

    public boolean instanceBelongsCurrentUser(UUID instanceId) {
        if (instanceId == null) {
            return false;
        }
        return instanceSessionService.findById(instanceId)
                .filter(is -> is.getUserId().equals(getUserId()))
                .isPresent();
    }

    public Boolean connectionBelongsCurrentUser(Long connectionId) {
        if (connectionId == null) {
            return false;
        }
        return connectionClient.connectionBelongsCurrentUser(connectionId).getBody();
    }

    public boolean accessKeyBelongsUser(Long accessKeyId) {
        if (accessKeyId == null) {
            return false;
        }
        Boolean result = accessKeyClient.validateMyAccessKeyById(accessKeyId).getBody();
        return nullToFalse(result);
    }

    @Component
    public static class Factory implements ExpressionMethodsFactory {
        private final InstanceSessionService instanceSessionService;
        private final AccessKeyClient accessKeyClient;
        private final ConnectionClient connectionClient;

        public Factory(InstanceSessionService instanceSessionService,
                       AccessKeyClient accessKeyClient, ConnectionClient connectionClient) {
            this.instanceSessionService = instanceSessionService;
            this.accessKeyClient = accessKeyClient;
            this.connectionClient = connectionClient;
        }

        @Override
        public String getNamespace() {
            return "metric";
        }

        @Override
        public Object create(Authentication authentication, MethodInvocation mi) {
            return new MetricExpressionMethods(authentication, instanceSessionService,
                    accessKeyClient, connectionClient);
        }
    }
}
