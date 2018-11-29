package io.electrica.connector.hub.security;

import io.electrica.common.security.CommonExpressionMethods;
import io.electrica.common.security.ExpressionMethodsFactory;
import io.electrica.connector.hub.repository.ConnectionRepository;
import io.electrica.user.feign.AccessKeyClient;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static io.electrica.common.helper.CollectionUtils.nullToFalse;

public class ConnectionExpressionMethods extends CommonExpressionMethods {

    private final ConnectionRepository connectionRepository;
    private final AccessKeyClient accessKeyClient;

    private ConnectionExpressionMethods(
            ConnectionRepository connectionRepository,
            AccessKeyClient accessKeyClient,
            Authentication authentication
    ) {
        super(authentication);
        this.connectionRepository = connectionRepository;
        this.accessKeyClient = accessKeyClient;
    }

    public boolean canUserAccess(Long connectionId) {
        return connectionRepository.canUserAccessConnection(connectionId, getUserId());
    }

    public boolean canUserAccessAuthorization(Long authorizationId) {
        return connectionRepository.canUserAccessAuthorization(authorizationId, getUserId());
    }

    public boolean isSessionAccessKey(Long accessKeyId) {
        Long sessionAccessKeyId = getIdentity().getAccessKeyId();
        return Objects.equals(accessKeyId, sessionAccessKeyId);
    }

    public boolean accessKeyBelongsUser(Long accessKeyId) {
        Boolean result = accessKeyClient.validateMyAccessKeyById(accessKeyId).getBody();
        return nullToFalse(result);
    }

    @Component
    public static class Factory implements ExpressionMethodsFactory {

        private final ConnectionRepository connectionRepository;
        private final AccessKeyClient accessKeyClient;

        public Factory(ConnectionRepository connectionRepository, AccessKeyClient accessKeyClient) {
            this.connectionRepository = connectionRepository;
            this.accessKeyClient = accessKeyClient;
        }

        @Override
        public String getNamespace() {
            return "connection";
        }

        @Override
        public Object create(Authentication authentication, MethodInvocation mi) {
            return new ConnectionExpressionMethods(connectionRepository, accessKeyClient, authentication);
        }
    }

}
