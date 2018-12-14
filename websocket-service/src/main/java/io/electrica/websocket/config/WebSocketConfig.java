package io.electrica.websocket.config;

import io.electrica.common.rest.PathConstants;
import io.electrica.websocket.context.SdkInstanceContextHandshakeInterceptor;
import io.electrica.websocket.session.WebSocketSessionHandler;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.springframework.beans.factory.BeanFactory;
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
import javax.inject.Named;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Inject
    private BeanFactory beanFactory;
    @Inject
    private SdkInstanceContextHandshakeInterceptor sdkInstanceContextHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        PerConnectionWebSocketHandler handler = new PerConnectionWebSocketHandler(
                WebSocketSessionHandler.class,
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
        return new DefaultHandshakeHandler(new JettyRequestUpgradeStrategy(policy));
    }

    @Named("ackTimeoutExecutorService")
    @Bean(destroyMethod = "shutdownNow")
    public ScheduledExecutorService ackTimeoutExecutorService() {
        return Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "amqp-ack-timeout-handler");
            thread.setDaemon(true);
            return thread;
        });
    }
}
