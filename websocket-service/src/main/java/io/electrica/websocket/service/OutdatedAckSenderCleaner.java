package io.electrica.websocket.service;

import io.electrica.websocket.amqp.AmqpAckSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class OutdatedAckSenderCleaner {

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "acks-sender-cleaner");
        thread.setDaemon(true);
        return thread;
    });
    private final ConcurrentMap<UUID, ConcurrentMap<UUID, AmqpAckSender>> tracked = new ConcurrentHashMap<>();

    private final long frequency;
    private final long webhookAckTimeout;

    @Inject
    public OutdatedAckSenderCleaner(
            @Value("${websocket.amqp.cleaner-frequency}") long frequency,
            @Value("${websocket.amqp.webhook.ack-timeout}") long webhookAckTimeout
    ) {
        this.frequency = frequency;
        this.webhookAckTimeout = webhookAckTimeout;
    }

    @PostConstruct
    public void schedule() {
        executor.scheduleWithFixedDelay(() -> {
            long startTime = System.currentTimeMillis();
            AtomicInteger removedWebhooks = new AtomicInteger();
            tracked.values().forEach(senders ->
                    senders.values().removeIf(sender -> {
                        AmqpAckSender.Type type = sender.getType();
                        switch (type) {
                            case Webhook:
                                return isRemoveWebhook(startTime, removedWebhooks, sender);
                            default:
                                throw new UnsupportedOperationException("Unsupported sender type: " + type);
                        }
                    })
            );
            long executionTime = System.currentTimeMillis() - startTime;
            log.info(
                    "Removed outdated AMQP Ack senders for {} millis: webhooks count = {}",
                    executionTime, removedWebhooks.get()
            );
        }, frequency, frequency, TimeUnit.MILLISECONDS);
    }

    private boolean isRemoveWebhook(long startTime, AtomicInteger removedWebhooks, AmqpAckSender sender) {
        boolean toRemove = (startTime - sender.getTimestamp()) > webhookAckTimeout;
        if (toRemove) {
            removedWebhooks.incrementAndGet();
        }
        return toRemove;
    }

    public UUID addCollection(ConcurrentMap<UUID, AmqpAckSender> item) {
        UUID id = UUID.randomUUID();
        tracked.put(id, item);
        return id;
    }

    public void removeCollection(UUID id) {
        tracked.remove(id);
    }

    @PreDestroy
    public void destroy() {
        executor.shutdownNow();
    }
}
