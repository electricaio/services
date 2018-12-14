package io.electrica.websocket.amqp;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;

public class AmqpAckContext {

    private final long deliveryTag;
    private final Channel channel;
    private final ScheduledFuture<?> ackTimeoutFuture;

    public AmqpAckContext(Message message, Channel channel, ScheduledFuture<?> ackTimeoutFuture) {
        this.deliveryTag = message.getMessageProperties().getDeliveryTag();
        this.channel = channel;
        this.ackTimeoutFuture = ackTimeoutFuture;
    }

    public void sendAck(boolean handled) throws IOException {
        boolean timeoutNotSent = cancel();
        if (timeoutNotSent) {
            if (handled) {
                channel.basicAck(deliveryTag, false);
            } else {
                sendReject();
            }
        }
    }

    public void sendReject() throws IOException {
        channel.basicReject(deliveryTag, true);
    }

    public boolean cancel() {
        return ackTimeoutFuture.cancel(false);
    }
}
