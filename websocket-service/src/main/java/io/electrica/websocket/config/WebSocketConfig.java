package io.electrica.websocket.config;

import io.electrica.common.rest.PathConstants;
import io.electrica.websocket.context.SdkInstanceContextHandshakeInterceptor;
import io.electrica.websocket.handler.ConnectionWebSocketHandler;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.jetty.JettyRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.inject.Inject;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Inject
    private BeanFactory beanFactory;
    @Inject
    private SdkInstanceContextHandshakeInterceptor sdkInstanceContextHandshakeInterceptor;

    @Value("${websocket.jetty.idle-timeout}")
    private int idleTimeout;
    @Value("${websocket.jetty.input-buffer-size}")
    private int inputBufferSize;
    @Value("${websocket.jetty.async-write-timeout}")
    private int asyncWriteTimeout;
    @Value("${websocket.jetty.max-text-message-size}")
    private int maxTextMessageSize;
    @Value("${websocket.jetty.max-text-message-buffer-size}")
    private int maxTextMessageBufferSize;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        PerConnectionWebSocketHandler handler = new PerConnectionWebSocketHandler(
                ConnectionWebSocketHandler.class,
                false
        );
        handler.setBeanFactory(beanFactory);

        registry.addHandler(handler, PathConstants.V1 + "/websockets")
                .setHandshakeHandler(jettyHandshakeHandler())
                .addInterceptors(sdkInstanceContextHandshakeInterceptor);
    }

    @Bean
    public HandshakeHandler jettyHandshakeHandler() {
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
        policy.setIdleTimeout(idleTimeout);
        policy.setInputBufferSize(inputBufferSize);
        policy.setAsyncWriteTimeout(asyncWriteTimeout);

        policy.setMaxBinaryMessageSize(1); // disabled
        policy.setMaxBinaryMessageBufferSize(1); // disabled

        policy.setMaxTextMessageSize(maxTextMessageSize);
        policy.setMaxTextMessageBufferSize(maxTextMessageBufferSize);


        return new DefaultHandshakeHandler(new JettyRequestUpgradeStrategy(policy));
    }

}
