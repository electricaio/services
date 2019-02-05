package io.electrica.metric.instance.session.event.config;

import io.electrica.common.condition.NotTestCondition;
import io.electrica.metric.common.mq.MetricAmqpService;
import io.electrica.metric.common.mq.instance.session.event.InstanceConnectionClosedEvent;
import io.electrica.metric.common.mq.instance.session.event.InstanceConnectionEstablishedEvent;
import io.electrica.metric.instance.session.service.dto.InstanceSessionDtoService;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import static io.electrica.metric.common.mq.config.MetricAmqpConfig.METRIC_EXCHANGE;
import static io.electrica.metric.common.mq.instance.session.InstanceSessionMetrics.CLOSED_ROUTING_KEY;
import static io.electrica.metric.common.mq.instance.session.InstanceSessionMetrics.ESTABLISHED_ROUTING_KEY;

@Configuration
@Conditional(NotTestCondition.class)
public class InstanceSessionAmqpConfig {
    public static final String INSTANCE_SESSION_ESTABLISHED_EVENT_QUEUE_NAME = METRIC_EXCHANGE
            + ".instance-session.established";
    public static final String INSTANCE_SESSION_CLOSED_EVENT_QUEUE_NAME = METRIC_EXCHANGE
            + ".instance-session.closed";

    private static final int NORMAL_CODE = 1000;

    @Bean
    public MessageListenerContainer establishedEventListener(MetricAmqpService metricAmqpService,
                                                             InstanceSessionDtoService instanceSessionDtoService) {
        metricAmqpService.declareAndBindQueue(INSTANCE_SESSION_ESTABLISHED_EVENT_QUEUE_NAME, ESTABLISHED_ROUTING_KEY);
        return metricAmqpService.buildListener(
                INSTANCE_SESSION_ESTABLISHED_EVENT_QUEUE_NAME,
                InstanceConnectionEstablishedEvent.class,
                (message, event) -> instanceSessionDtoService.start(event.getDescriptor())
        );
    }

    @Bean
    public MessageListenerContainer closedEventListener(MetricAmqpService metricAmqpService,
                                                        InstanceSessionDtoService instanceSessionDtoService) {
        metricAmqpService.declareAndBindQueue(INSTANCE_SESSION_CLOSED_EVENT_QUEUE_NAME, CLOSED_ROUTING_KEY);
        return metricAmqpService.buildListener(
                INSTANCE_SESSION_CLOSED_EVENT_QUEUE_NAME,
                InstanceConnectionClosedEvent.class,
                (message, event) -> {
                    if (NORMAL_CODE == event.getCode()) {
                        instanceSessionDtoService.stop(event.getDescriptor());
                    } else {
                        instanceSessionDtoService.expire(event.getDescriptor());
                    }
                }
        );
    }
}
