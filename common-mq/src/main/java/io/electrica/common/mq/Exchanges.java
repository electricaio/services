package io.electrica.common.mq;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;

public class Exchanges {

    private static final String WEBHOOKS = "webhooks";

    public static Exchange newWebhooks() {
        return ExchangeBuilder.topicExchange(WEBHOOKS).build();
    }

}
