package io.electrica.websocket.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import io.electrica.common.context.Identity;
import io.electrica.common.mq.webhook.WebhookQueueDispatcher;
import io.electrica.webhook.message.WebhookMessage;
import io.electrica.websocket.amqp.AmqpAckContext;
import io.electrica.websocket.amqp.WebhookMessageListenerContainerFactory;
import io.electrica.websocket.context.SdkInstanceContext;
import io.electrica.websocket.dto.inbound.AckInboundMessage;
import io.electrica.websocket.dto.outbound.OutboundMessage;
import io.electrica.websocket.dto.outbound.WebhookOutboundMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.*;

@Slf4j
@Validated
public class WebSocketSessionMessageDispatcher implements DisposableBean {

    private final ObjectMapper objectMapper;
    private final WebhookQueueDispatcher webhookQueueDispatcher;
    private final WebhookMessageListenerContainerFactory webhookMessageListenerContainerFactory;
    private final ScheduledExecutorService ackTimeoutExecutor;

    private final long webhookAckTimeout;

    private volatile WebSocketSession session;
    private volatile MessageListenerContainer webhookListenerContainer;
    private volatile ConcurrentMap<UUID, AmqpAckContext> ackContextsByMessageId = new ConcurrentHashMap<>();

    @Inject
    public WebSocketSessionMessageDispatcher(
            ObjectMapper objectMapper,
            WebhookQueueDispatcher webhookQueueDispatcher,
            WebhookMessageListenerContainerFactory webhookMessageListenerContainerFactory,
            @Named("ackTimeoutExecutorService") ScheduledExecutorService ackTimeoutExecutor,
            @Value("${websocket.amqp.webhook.ackTimeout}") long webhookAckTimeout
    ) {
        this.objectMapper = objectMapper;
        this.webhookQueueDispatcher = webhookQueueDispatcher;
        this.webhookMessageListenerContainerFactory = webhookMessageListenerContainerFactory;
        this.webhookAckTimeout = webhookAckTimeout;
        this.ackTimeoutExecutor = ackTimeoutExecutor;
    }

    void init(SdkInstanceContext instanceContext, WebSocketSession session) {
        this.session = session;

        Identity identity = instanceContext.getIdentity();
        String webhookQueue = webhookQueueDispatcher.createQueueIfAbsent(
                identity.getOrganizationId(),
                identity.getUserId(),
                identity.getAccessKeyId()
        );

        webhookListenerContainer = webhookMessageListenerContainerFactory.create(
                webhookQueue,
                this::handleWebhookAmqpMessage
        );
        webhookListenerContainer.start();
    }

    private void handleWebhookAmqpMessage(Message message, Channel channel) throws IOException {
        WebhookOutboundMessage webhookMessage = buildWebhookMessage(message);
        UUID messageId = webhookMessage.getId();

        ScheduledFuture<?> ackTimeoutFuture = ackTimeoutExecutor.schedule(() -> {
            AmqpAckContext context = ackContextsByMessageId.remove(messageId);
            if (context != null) {
                try {
                    context.sendReject();
                } catch (IOException e) {
                    log.error("Can't handle ack timeout for message id: " + messageId, e);
                }
            }
        }, webhookAckTimeout, TimeUnit.MILLISECONDS);

        AmqpAckContext ackContext = new AmqpAckContext(message, channel, ackTimeoutFuture);
        ackContextsByMessageId.put(messageId, ackContext);

        send(webhookMessage);
    }

    void handleWebSocketMessage(@Valid AckInboundMessage ack) throws IOException {
        UUID correlationId = ack.getCorrelationId();
        AmqpAckContext context = ackContextsByMessageId.remove(correlationId);
        if (context != null) {
            context.sendAck(ack.getAccepted());
        }
    }

    private WebhookOutboundMessage buildWebhookMessage(Message message) throws IOException {
        WebhookMessage webhookMessage = objectMapper.readValue(message.getBody(), WebhookMessage.class);
        return new WebhookOutboundMessage(webhookMessage);
    }

    private void send(OutboundMessage message) throws IOException {
        String payload = objectMapper.writeValueAsString(message);
        session.sendMessage(new TextMessage(payload));
    }

    @Override
    public void destroy() {
        if (webhookListenerContainer != null) {
            webhookListenerContainer.stop();
        }
        ackContextsByMessageId.values().forEach(AmqpAckContext::cancel);
        ackContextsByMessageId.clear();
    }

}
