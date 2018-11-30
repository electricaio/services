package io.electrica.webhook.config;

import io.electrica.common.mq.Exchanges;
import org.springframework.amqp.core.Exchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebhookRabbitConfig {

    @Bean
    public Exchange webhooksExchange() {
        return Exchanges.newWebhooks();
    }

}
