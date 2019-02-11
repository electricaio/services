package io.electrica.metric.connection.invocation.event.config;

import com.github.dozermapper.core.Mapper;
import io.electrica.common.condition.NotTestCondition;
import io.electrica.metric.common.mq.MetricAmqpService;
import io.electrica.metric.common.mq.connection.invocation.event.ConnectionInvocationEvent;
import io.electrica.metric.common.mq.connection.invocation.event.ConnectionInvocationResultEvent;
import io.electrica.metric.connection.invocation.dto.ConnectionInvocationDto;
import io.electrica.metric.connection.invocation.model.ConnectionInvocationStatus;
import io.electrica.metric.connection.invocation.service.dto.ConnectionInvocationDtoService;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import static io.electrica.metric.common.mq.config.MetricAmqpConfig.METRIC_EXCHANGE;
import static io.electrica.metric.common.mq.connection.invocation.ConnectionInvocationMetrics.CONNECTION_INVOCATION_RESULT_ROUTING_KEY;
import static io.electrica.metric.common.mq.connection.invocation.ConnectionInvocationMetrics.CONNECTION_INVOCATION_ROUTING_KEY;

@Configuration
@Conditional(NotTestCondition.class)
public class ConnectionInvocationAmqpConfig {
    public static final String CONNECTION_INVOCATION_EVENT_QUEUE_NAME = METRIC_EXCHANGE
            + ".connection-invocation.invoked";
    public static final String CONNECTION_INVOCATION_RESULT_EVENT_QUEUE_NAME = METRIC_EXCHANGE
            + ".connection-invocation.result";

    @Bean
    public MessageListenerContainer connectionInvocation(MetricAmqpService metricAmqpService,
                                                         ConnectionInvocationDtoService service,
                                                         Mapper mapper) {
        metricAmqpService.declareAndBindQueue(CONNECTION_INVOCATION_EVENT_QUEUE_NAME,
                CONNECTION_INVOCATION_ROUTING_KEY);
        return metricAmqpService.buildListener(
                CONNECTION_INVOCATION_EVENT_QUEUE_NAME,
                ConnectionInvocationEvent.class,
                (message, event) -> {
                    ConnectionInvocationDto dto = mapper.map(event, ConnectionInvocationDto.class);
                    dto.setStatus(ConnectionInvocationStatus.Pending);
                    service.upsert(dto);
                }
        );
    }

    @Bean
    public MessageListenerContainer connectionInvocationResult(MetricAmqpService metricAmqpService,
                                                               ConnectionInvocationDtoService service,
                                                               Mapper mapper) {
        metricAmqpService.declareAndBindQueue(CONNECTION_INVOCATION_RESULT_EVENT_QUEUE_NAME,
                CONNECTION_INVOCATION_RESULT_ROUTING_KEY);
        return metricAmqpService.buildListener(
                CONNECTION_INVOCATION_RESULT_EVENT_QUEUE_NAME,
                ConnectionInvocationResultEvent.class,
                (message, event) -> {
                    ConnectionInvocationDto dto = mapper.map(event, ConnectionInvocationDto.class);
                    dto.setStatus(event.getSuccess() ?
                            ConnectionInvocationStatus.Success : ConnectionInvocationStatus.Error);
                    service.upsert(dto);
                }
        );
    }
}
