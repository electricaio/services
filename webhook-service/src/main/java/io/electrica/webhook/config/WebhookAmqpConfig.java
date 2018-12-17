package io.electrica.webhook.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.electrica.common.condition.NotTestCondition;
import io.electrica.common.mq.webhook.WebhookResultMessages;
import io.electrica.webhook.dto.MessageResultDto;
import io.electrica.webhook.service.WebhookMessageResultDispatcher;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.UUID;

import static io.electrica.webhook.config.WebhookConfig.INSTANCE_ID_QUALIFIER;

@Configuration
@Conditional(NotTestCondition.class)
public class WebhookAmqpConfig {

    @Inject
    @Named(INSTANCE_ID_QUALIFIER)
    private UUID serviceInstanceId;

    @Value("${mq.webhook.message-result.queue-ttl}")
    private Long resultMessageQueueTtl;

    @Value("${mq.webhook.message-result.message-ttl}")
    private Long resultMessageMessageTtl;

    @Value("${webhook.message-result.amqp.prefetchCount}")
    private int resultMessagePrefetchCount;

    @Inject
    private WebhookMessageResultDispatcher webhookMessageResultDispatcher;

    @Inject
    private ObjectMapper objectMapper;

    @Bean
    public Exchange messageResultExchange() {
        return WebhookResultMessages.newExchange();
    }

    @Bean
    public Queue instanceMessageResultQueue() {
        String queueName = WebhookResultMessages.queueName(serviceInstanceId);
        return WebhookResultMessages.newQueue(queueName, resultMessageMessageTtl, resultMessageQueueTtl);
    }

    @Bean
    public Binding instanceMessageResultQueueBinding() {
        String routingKey = WebhookResultMessages.routingKey(serviceInstanceId);
        return WebhookResultMessages.newBinding(instanceMessageResultQueue(), messageResultExchange(), routingKey);
    }

    @Bean
    public MessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
        DirectMessageListenerContainer container = new DirectMessageListenerContainer(connectionFactory);
        container.setQueueNames(instanceMessageResultQueue().getName());
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setPrefetchCount(resultMessagePrefetchCount);
        container.setDefaultRequeueRejected(false);
        container.setChannelAwareMessageListener((message, channel) -> {
            MessageResultDto messageResult = objectMapper.readValue(message.getBody(), MessageResultDto.class);
            webhookMessageResultDispatcher.handle(messageResult);
        });
        return container;
    }

}
