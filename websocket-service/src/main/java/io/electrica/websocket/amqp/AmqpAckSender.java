package io.electrica.websocket.amqp;

import com.rabbitmq.client.Channel;
import lombok.Getter;
import org.springframework.amqp.core.Message;

import java.io.IOException;

public class AmqpAckSender {

    private final long deliveryTag;
    private final Channel channel;
    @Getter
    private final Type type;
    @Getter
    private final long timestamp = System.currentTimeMillis();

    public AmqpAckSender(Message message, Channel channel, Type type) {
        this.deliveryTag = message.getMessageProperties().getDeliveryTag();
        this.channel = channel;
        this.type = type;
    }

    public void send(boolean accepted) throws IOException {
        if (accepted) {
            channel.basicAck(deliveryTag, false);
        } else {
            channel.basicNack(deliveryTag, false, true);
        }
    }

    public enum Type {
        Webhook, Result
    }
}
