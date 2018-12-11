package io.electrica.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.electrica.websocket.context.SdkInstanceContext;
import io.electrica.websocket.dto.inbound.AckInboundMessage;
import io.electrica.websocket.dto.outbound.OutboundMessage;
import io.electrica.websocket.dto.outbound.WebhookOutboundMessage;
import lombok.Setter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.inject.Inject;
import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

@Validated
public class ConnectionMessageHandler {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Setter
    private SdkInstanceContext instanceContext;
    @Setter
    private WebSocketSession session;

    @Inject
    public ConnectionMessageHandler(ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void handle(@Valid AckInboundMessage ack) throws IOException {
        //ToDo dummy

        WebhookOutboundMessage outboundMessage = new WebhookOutboundMessage();
        outboundMessage.setId(UUID.randomUUID());
//        outboundMessage.setCorrelationId(ack.getCorrelationId());
//        outboundMessage.setArguments(Collections.singletonMap("arg", "hello-world"));

        send(outboundMessage);
    }

    private void send(OutboundMessage outboundMessage) throws IOException {
        String payload = objectMapper.writeValueAsString(outboundMessage);
        session.sendMessage(new TextMessage(payload));
    }

}
