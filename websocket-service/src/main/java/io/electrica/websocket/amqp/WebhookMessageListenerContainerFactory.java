package io.electrica.websocket.amqp;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

@Component
public class WebhookMessageListenerContainerFactory {

    private final ConnectionFactory connectionFactory;
    private final long ackTimeout;
    private final int prefetchCount;

    private final ThreadPoolTaskScheduler consumerTaskScheduler;

    @Inject
    public WebhookMessageListenerContainerFactory(
            ConnectionFactory connectionFactory,
            @Value("${websocket.amqp.webhook.ack-timeout}") long ackTimeout,
            @Value("${websocket.amqp.webhook.prefetch-count}") int prefetchCount,
            @Value("${websocket.amqp.webhook.consumerPoolSize}") int consumerPoolSize
    ) {
        this.connectionFactory = connectionFactory;
        this.ackTimeout = ackTimeout;
        this.prefetchCount = prefetchCount;
        this.consumerTaskScheduler = createThreadPoolTaskScheduler("amqp-webhook-consumer-", consumerPoolSize);
    }

    private static ThreadPoolTaskScheduler createThreadPoolTaskScheduler(String threadNamePrefix, int poolSize) {
        ThreadPoolTaskScheduler result = new ThreadPoolTaskScheduler();
        result.setThreadNamePrefix(threadNamePrefix);
        result.setPoolSize(poolSize);
        result.afterPropertiesSet();
        return result;
    }

    public MessageListenerContainer create(String queue, ChannelAwareMessageListener messageListener) {
        DirectMessageListenerContainer container = new DirectMessageListenerContainer(connectionFactory);
        container.setQueueNames(queue);
        container.setAckTimeout(ackTimeout);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMissingQueuesFatal(true);
        container.setChannelAwareMessageListener(messageListener);
        container.setPrefetchCount(prefetchCount);

        container.setTaskScheduler(consumerTaskScheduler);
        container.setTaskExecutor(consumerTaskScheduler);

        container.afterPropertiesSet();
        return container;
    }

    @PreDestroy
    public void shutdown() {
        consumerTaskScheduler.shutdown();
    }
}
