package io.electrica.websocket.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.electrica.common.context.Identity;
import io.electrica.common.mq.webhook.WebhookQueueDispatcher;
import io.electrica.webhook.message.WebhookMessage;
import io.electrica.websocket.amqp.AmqpAckSender;
import io.electrica.websocket.amqp.WebhookMessageListenerContainerFactory;
import io.electrica.websocket.context.SdkInstanceContext;
import io.electrica.websocket.dto.inbound.AckInboundMessage;
import io.electrica.websocket.dto.outbound.OutboundMessage;
import io.electrica.websocket.dto.outbound.WebhookOutboundMessage;
import io.electrica.websocket.service.OutdatedAckSenderCleaner;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.inject.Inject;
import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Objects.requireNonNull;

@Validated
public class WebSocketSessionMessageDispatcher implements DisposableBean {

    private final ObjectMapper objectMapper;
    private final WebhookQueueDispatcher webhookQueueDispatcher;
    private final WebhookMessageListenerContainerFactory webhookMessageListenerContainerFactory;
    private final OutdatedAckSenderCleaner ackSenderCleaner;

    private UUID cleanerId;
    private WebSocketSession session;
    private MessageListenerContainer webhookListenerContainer;
    private ConcurrentMap<UUID, AmqpAckSender> ackSenders = new ConcurrentHashMap<>();

    @Inject
    public WebSocketSessionMessageDispatcher(
            ObjectMapper objectMapper,
            WebhookQueueDispatcher webhookQueueDispatcher,
            WebhookMessageListenerContainerFactory webhookMessageListenerContainerFactory,
            OutdatedAckSenderCleaner ackSenderCleaner
    ) {
        this.objectMapper = objectMapper;
        this.webhookQueueDispatcher = webhookQueueDispatcher;
        this.webhookMessageListenerContainerFactory = webhookMessageListenerContainerFactory;
        this.ackSenderCleaner = ackSenderCleaner;
    }

    void init(SdkInstanceContext instanceContext, WebSocketSession session) {
        this.session = session;

        Identity identity = instanceContext.getIdentity();
        String webhookQueue = webhookQueueDispatcher.createQueueIfAbsent(
                identity.getOrganizationId(),
                identity.getUserId(),
                identity.getAccessKeyId()
        );

        cleanerId = ackSenderCleaner.addCollection(ackSenders);

        webhookListenerContainer = webhookMessageListenerContainerFactory.create(webhookQueue, (message, channel) -> {
            UUID id = sendWebhook(message);
            ackSenders.put(id, new AmqpAckSender(message, channel, AmqpAckSender.Type.Webhook));
        });
        webhookListenerContainer.start();
    }

    void handle(@Valid AckInboundMessage ack) throws IOException {
        UUID correlationId = ack.getCorrelationId();
        AmqpAckSender ackSender = ackSenders.remove(correlationId);
        if (ackSender != null) {
            ackSender.send(ack.getAccepted());
        }
    }

    private UUID sendWebhook(Message message) throws IOException {
        WebhookMessage webhookMessage = objectMapper.readValue(message.getBody(), WebhookMessage.class);
        WebhookOutboundMessage outboundMessage = new WebhookOutboundMessage(webhookMessage);
        return send(outboundMessage);
    }

    private UUID send(OutboundMessage outboundMessage) throws IOException {
        String payload = objectMapper.writeValueAsString(outboundMessage);
        session.sendMessage(new TextMessage(payload));
        return outboundMessage.getId();
    }

    @Override
    public void destroy() {
        requireNonNull(webhookListenerContainer, "webhookListenerContainer");
        requireNonNull(cleanerId, "cleanerId");
        requireNonNull(ackSenders, "ackSenders");

        webhookListenerContainer.stop();
        ackSenderCleaner.removeCollection(cleanerId);
        ackSenders.clear();
    }

}
