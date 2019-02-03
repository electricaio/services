package io.electrica.metric.instance.session.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import io.electrica.metric.common.mq.MetricEvent;
import io.electrica.metric.common.mq.instance.session.InstanceSessionMetrics;
import io.electrica.metric.common.mq.instance.session.event.InstanceConnectionClosedEvent;
import io.electrica.metric.common.mq.instance.session.event.InstanceConnectionEstablishedEvent;
import io.electrica.metric.instance.session.service.dto.InstanceSessionDtoService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class InstanceSessionEventHandler implements ChannelAwareMessageListener {
    private static final int NORMAL_CODE = 1000;
    private final ObjectMapper objectMapper;
    private final InstanceSessionDtoService instanceSessionDtoService;

    @Inject
    public InstanceSessionEventHandler(ObjectMapper objectMapper, InstanceSessionDtoService instanceSessionDtoService) {
        this.objectMapper = objectMapper;
        this.instanceSessionDtoService = instanceSessionDtoService;
    }

    public void handleEstablished(InstanceConnectionEstablishedEvent event) {
        instanceSessionDtoService.start(event.getDescriptor());
    }

    public void handleClosed(InstanceConnectionClosedEvent event) {
        if (NORMAL_CODE == event.getCode()) {
            instanceSessionDtoService.stop(event.getDescriptor());
        } else {
            instanceSessionDtoService.expire(event.getDescriptor());
        }
    }

    public void handle(MetricEvent event) {
        if (event instanceof InstanceConnectionEstablishedEvent) {
            handleEstablished((InstanceConnectionEstablishedEvent) event);
        } else if (event instanceof InstanceConnectionClosedEvent) {
            handleClosed((InstanceConnectionClosedEvent) event);
        } else {
            throw new IllegalArgumentException("Unsupported type of event");
        }
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        Class<? extends MetricEvent> clazz = InstanceSessionMetrics.KEY_TO_TYPE.get(routingKey);
        MetricEvent instanceSessionEvent = objectMapper.readValue(message.getBody(), clazz);
        handle(instanceSessionEvent);
    }
}
