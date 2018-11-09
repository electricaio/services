package io.electrica.connector.hub.security;

import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityImpl;
import io.electrica.common.security.ExpressionMethodsFactory;
import io.electrica.connector.hub.repository.ConnectionRepository;
import io.electrica.user.feign.AccessKeyClient;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static io.electrica.common.helper.CollectionUtils.nullToFalse;

public class ConnectionExpressionMethods {

    private final ConnectionRepository connectionRepository;
    private final AccessKeyClient accessKeyClient;
    private final Authentication authentication;

    private ConnectionExpressionMethods(
            ConnectionRepository connectionRepository,
            AccessKeyClient accessKeyClient,
            Authentication authentication
    ) {
        this.connectionRepository = connectionRepository;
        this.accessKeyClient = accessKeyClient;
        this.authentication = authentication;
    }

    public Identity getIdentity() {
        return new IdentityImpl(authentication);
    }

    public boolean canUserAccess(Long connectionId) {
        final Long userId = getIdentity().getUserId();
        return connectionRepository.canUserAccessConnection(connectionId, userId);
    }

    public boolean canUserAccessAuthorization(Long authorizationId) {
        final Long userId = getIdentity().getUserId();
        return connectionRepository.canUserAccessAuthorization(authorizationId, userId);
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