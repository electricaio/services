package io.electrica.websocket.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.electrica.websocket.context.SdkInstanceContext;
import io.electrica.websocket.dto.inbound.AckInboundMessage;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.inject.Inject;
import java.io.IOException;

import static io.electrica.websocket.context.SdkInstanceContextHandshakeInterceptor.INSTANCE_CONTEXT_ATTRIBUTE;

public class WebSocketSessionHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final AutowireCapableBeanFactory beanFactory;
    private final InstanceSessionChangeSender instanceSessionChangeSender;

    private volatile WebSocketSessionMessageDispatcher messageHandler;
    private volatile SdkInstanceContext sdkInstanceContext;

    @Inject
    public WebSocketSessionHandler(ObjectMapper objectMapper, AutowireCapableBeanFactory beanFactory,
                                   InstanceSessionChangeSender instanceSessionChangeSender) {
        this.objectMapper = objectMapper;
        this.beanFactory = beanFactory;
        this.instanceSessionChangeSender = instanceSessionChangeSender;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sdkInstanceContext = extractContext(session);
        messageHandler = beanFactory.createBean(WebSocketSessionMessageDispatcher.class);
        messageHandler.init(sdkInstanceContext, session);
        instanceSessionChangeSender.start(sdkInstanceContext);
    }

    private SdkInstanceContext extractContext(WebSocketSession session) {
        Object context = session.getAttributes().get(INSTANCE_CONTEXT_ATTRIBUTE);
        if (!(context instanceof SdkInstanceContext)) {
            throw new IllegalStateException("Sdk instance context expected: " + INSTANCE_CONTEXT_ATTRIBUTE);
        }

        return  (SdkInstanceContext) context;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        AckInboundMessage ack = objectMapper.readValue(message.getPayload(), AckInboundMessage.class);
        messageHandler.handleWebSocketMessage(ack);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        if (messageHandler != null) {
            beanFactory.destroyBean(messageHandler);
        }
        instanceSessionChangeSender.stop(sdkInstanceContext, status);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        throw new UnsupportedOperationException("Not supported binary message");
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) {
        throw new UnsupportedOperationException("Not supported pong message");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        throw new IOException("Transport error", exception);
    }
}
