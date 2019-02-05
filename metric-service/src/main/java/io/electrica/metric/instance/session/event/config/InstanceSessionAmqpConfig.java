package io.electrica.metric.instance.session.event.config;

import io.electrica.common.condition.NotTestCondition;
import io.electrica.metric.common.mq.MetricAmqpService;
import io.electrica.metric.instance.session.event.InstanceSessionEventHandler;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import static io.electrica.metric.common.mq.config.MetricAmqpConfig.METRIC_EXCHANGE;
import static io.electrica.metric.common.mq.instance.session.InstanceSessionMetrics.ROUTING_PREFIX;

@Configuration
@Conditional(NotTestCondition.class)
public class InstanceSessionAmqpConfig {
    public static final String INSTANCE_SESSION_QUEUE_ROUTING_KEY = ROUTING_PREFIX + "*";
    public static final String INSTANCE_SESSION_QUEUE_NAME = METRIC_EXCHANGE + ".instance-session";

    @Bean
    public MessageListenerContainer messageListenerContainer(MetricAmqpService metricAmqpService,
                                                             InstanceSessionEventHandler instanceSessionEventHandler) {
        metricAmqpService.declareAndBindQueue(INSTANCE_SESSION_QUEUE_NAME, INSTANCE_SESSION_QUEUE_ROUTING_KEY);
        return metricAmqpService.buildListener(INSTANCE_SESSION_QUEUE_NAME, instanceSessionEventHandler);
    }
}
