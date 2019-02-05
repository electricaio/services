package io.electrica.metric.common.mq.config;

import io.electrica.common.condition.NotTestCondition;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.inject.Named;

@EnableRabbit
@Configuration
@Conditional(NotTestCondition.class)
public class MetricAmqpConfig {
    public static final String METRIC_EXCHANGE = "metric-message";
    public static final String METRIC_EXCHANGE_QUALIFIER = "metricExchange";
    public static final String METRIC_DEAD_LETTER_EXCHANGE = "metricDeadLetterExchange";
    public static final String METRIC_DEAD_LETTER_EXCHANGE_QUALIFIER = "deadLetterMetricExchange";

    @Bean
    @Named(METRIC_EXCHANGE_QUALIFIER)
    public Exchange metricExchange() {
        return ExchangeBuilder.topicExchange(METRIC_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    @Named(METRIC_DEAD_LETTER_EXCHANGE_QUALIFIER)
    public Exchange metricDeadLetterExchange() {
        return ExchangeBuilder.directExchange(METRIC_DEAD_LETTER_EXCHANGE)
                .durable(true)
                .build();
    }
}
