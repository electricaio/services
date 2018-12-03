package io.electrica.webhook.config;

import io.electrica.common.mq.WebhookMessages;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebhookRabbitConfig {

    @Bean
    public Exchange webhooksExchange() {
        return WebhookMessages.newExchange();
    }

    @Bean
    public MessagePostProcessor webhooksMessagePostProcessor() {
        return message -> {
            MessageProperties properties = message.getMessageProperties();
            properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        };
    }

}
