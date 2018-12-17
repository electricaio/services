package io.electrica.common.mq;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;

public class PersistentMessagePostProcessor implements MessagePostProcessor {

    public static final MessagePostProcessor INSTANCE = new PersistentMessagePostProcessor();

    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        MessageProperties properties = message.getMessageProperties();
        properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        return message;
    }
}
