package io.electrica.metric.common.mq.instance.session.config;

import io.electrica.common.condition.NotTestCondition;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.inject.Named;

import static io.electrica.metric.common.mq.instance.session.InstanceSessionMetrics.*;

@Configuration
@Conditional(NotTestCondition.class)
public class InstanceSessionMetricConfig {
    public static final String INSTANCE_SESSION_METRIC_EXCHANGE_QUALIFIER = "instanceSessionMetricExchange";
    public static final String INSTANCE_SESSION_METRIC_QUEUE_QUALIFIER = "instanceSessionMetricQueue";

    @Bean
    @Named(INSTANCE_SESSION_METRIC_EXCHANGE_QUALIFIER)
    public Exchange instanceSessionMetricExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    @Named(INSTANCE_SESSION_METRIC_QUEUE_QUALIFIER)
    public Queue instanceSessionMetricQueue(@Value("${mq.metric.message.instance-session.message-ttl}") Long messageTtl,
                                            @Value("${mq.metric.message.instance-session.queue-ttl}") Long queueTtl) {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-message-ttl", messageTtl)
                .withArgument("x-expires", queueTtl)
                .build();
    }

    @Bean
    public Binding instanceSessionMetricBinding(
            @Named(INSTANCE_SESSION_METRIC_EXCHANGE_QUALIFIER) Exchange exchange,
            @Named(INSTANCE_SESSION_METRIC_QUEUE_QUALIFIER) Queue queue
    ) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(QUEUE_ROUTING_KEY)
                .noargs();
    }
}
