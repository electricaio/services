package io.electrica.metric.webhook.invocation.event.config;

import com.github.dozermapper.core.Mapper;
import io.electrica.common.condition.NotTestCondition;
import io.electrica.metric.common.mq.MetricAmqpService;
import io.electrica.metric.common.mq.webhook.invocation.event.WebhookInvocationErrorEvent;
import io.electrica.metric.common.mq.webhook.invocation.event.WebhookInvocationEvent;
import io.electrica.metric.common.mq.webhook.invocation.event.WebhookInvocationResultEvent;
import io.electrica.metric.webhook.invocation.dto.WebhookInvocationDto;
import io.electrica.metric.webhook.invocation.service.WebhookInvocationDtoService;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import static io.electrica.metric.common.mq.config.MetricAmqpConfig.METRIC_EXCHANGE;
import static io.electrica.metric.common.mq.webhook.invocation.WebhookInvcoationMetrics.*;

@Configuration
@Conditional(NotTestCondition.class)
public class WebhookInvocationAmqpConfig {
    public static final String WEBHOOK_INVOCATION_EVENT_QUEUE_NAME = METRIC_EXCHANGE
            + ".webhook-invocation.invoked";
    public static final String WEBHOOK_INVOCATION_RESULT_EVENT_QUEUE_NAME = METRIC_EXCHANGE
            + ".webhook-invocation.result";
    public static final String WEBHOOK_INVOCATION_ERROR_EVENT_QUEUE_NAME = METRIC_EXCHANGE
            + ".webhook-invocation.error";

    @Bean
    public MessageListenerContainer webhookInvocation(MetricAmqpService metricAmqpService,
                                                      WebhookInvocationDtoService service,
                                                      Mapper mapper) {
        metricAmqpService.declareAndBindQueue(WEBHOOK_INVOCATION_EVENT_QUEUE_NAME,
                WEBHOOK_INVOCATION_ROUTING_KEY);
        return metricAmqpService.buildListener(
                WEBHOOK_INVOCATION_EVENT_QUEUE_NAME,
                WebhookInvocationEvent.class,
                (message, event) -> service.upsert(mapper.map(event, WebhookInvocationDto.class))
        );
    }

    @Bean
    public MessageListenerContainer webhookInvocationResult(MetricAmqpService metricAmqpService,
                                                            WebhookInvocationDtoService service,
                                                            Mapper mapper) {
        metricAmqpService.declareAndBindQueue(WEBHOOK_INVOCATION_RESULT_EVENT_QUEUE_NAME,
                WEBHOOK_INVOCATION_RESULT_ROUTING_KEY);
        return metricAmqpService.buildListener(
                WEBHOOK_INVOCATION_RESULT_EVENT_QUEUE_NAME,
                WebhookInvocationResultEvent.class,
                (message, event) -> service.upsert(mapper.map(event, WebhookInvocationDto.class))
        );
    }

    @Bean
    public MessageListenerContainer webhookInvocationError(MetricAmqpService metricAmqpService,
                                                           WebhookInvocationDtoService service,
                                                           Mapper mapper) {
        metricAmqpService.declareAndBindQueue(WEBHOOK_INVOCATION_ERROR_EVENT_QUEUE_NAME,
                WEBHOOK_INVOCATION_ERROR_ROUTING_KEY);
        return metricAmqpService.buildListener(
                WEBHOOK_INVOCATION_ERROR_EVENT_QUEUE_NAME,
                WebhookInvocationErrorEvent.class,
                (message, event) -> service.upsert(mapper.map(event, WebhookInvocationDto.class))
        );
    }
}
