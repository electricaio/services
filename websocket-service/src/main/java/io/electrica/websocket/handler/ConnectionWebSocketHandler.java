package io.electrica.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.electrica.websocket.context.SdkInstanceContext;
import io.electrica.websocket.dto.inbound.InboundMessage;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.BeanCreatingHandlerProvider;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.inject.Inject;

import static io.electrica.websocket.context.SdkInstanceContextHandshakeInterceptor.INSTANCE_CONTEXT_ATTRIBUTE;

public class ConnectionWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final BeanCreatingHandlerProvider<ConnectionMessageHandler> beanCreatingHandlerProvider =
            new BeanCreatingHandlerProvider<>(ConnectionMessageHandler.class);

    private volatile ConnectionMessageHandler messageHandler;

    @Inject
    public ConnectionWebSocketHandler(ObjectMapper objectMapper, BeanFactory beanFactory) {
        this.objectMapper = objectMapper;
        beanCreatingHandlerProvider.setBeanFactory(beanFactory);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Object context = session.getAttributes().get(INSTANCE_CONTEXT_ATTRIBUTE);
        if (!(context instanceof SdkInstanceContext)) {
            throw new IllegalStateException("Sdk instance context expected: " + INSTANCE_CONTEXT_ATTRIBUTE);
        }

        SdkInstanceContext sdkInstanceContext = (SdkInstanceContext) context;
        messageHandler = beanCreatingHandlerProvider.getHandler();
        messageHandler.setInstanceContext(sdkInstanceContext);
        messageHandler.setSession(session);

        //ToDo initialize subscriptions here
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        InboundMessage inboundMessage = objectMapper.readValue(message.getPayload(), InboundMessage.class);
        messageHandler.handle(inboundMessage);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // ToDo unsibscribe here
        if (messageHandler != null) {
            beanCreatingHandlerProvider.destroy(messageHandler);
        }
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        throw new UnsupportedOperationException("Not supported binary message");
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        throw new UnsupportedOperationException("Not supported pong message");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        throw new Exception("Transport error", exception);
    }
}
