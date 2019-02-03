package io.electrica.metric.instance.session.event.config;

import io.electrica.metric.instance.session.event.InstanceSessionEventHandler;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.electrica.metric.common.mq.instance.session.InstanceSessionMetrics.QUEUE_NAME;

@Configuration
public class InstanceSessionListenerConfig {
    @Bean
    public MessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory,
                                                             InstanceSessionEventHandler instanceSessionEventHandler) {
        DirectMessageListenerContainer container = new DirectMessageListenerContainer(connectionFactory);
        container.setQueueNames(QUEUE_NAME);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setDefaultRequeueRejected(false);
        container.setChannelAwareMessageListener(instanceSessionEventHandler);
        container.afterPropertiesSet();
        return container;
    }
}
