package io.electrica.it.integration.slack;

import io.electrica.connector.hub.dto.ConnectionDto;
import io.electrica.connector.hub.dto.ConnectorDto;
import io.electrica.it.BaseIT;
import io.electrica.sdk.java.core.Electrica;
import io.electrica.sdk.java.core.ResponseHandler;
import io.electrica.sdk.java.core.exception.IntegrationException;
import io.electrica.sdk.java.core.http.impl.SingleInstanceHttpModule;
import io.electrica.sdk.java.slack.channel.v1.SlackChannelV1;
import io.electrica.sdk.java.slack.channel.v1.SlackChannelV1Manager;
import io.electrica.sdk.java.slack.channel.v1.model.ChannelV1SendMessageResult;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.FullAccessKeyDto;
import io.electrica.user.dto.UserDto;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static io.electrica.it.util.ItServiceConstants.*;
import static org.testng.Assert.assertTrue;

public class SlackV1Test extends BaseIT {

    @Value("${it-service.invoker-service.url}")
    private String invokerServiceUrl;

    @Value("${it-service.slack.v1.channel}")
    private String slackChannel;

    private Electrica instance;
    private UserDto user;

    @BeforeGroups(groups = {TEST_GROUP})
    public void init() {
        user = contextHolder.getUsers().get(0);
        contextHolder.setContextForUser(user.getEmail());
        AccessKeyDto accessKeyDto = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody().get(0);
        FullAccessKeyDto fullAccessKeyDto = accessKeyClient.getAccessKey(accessKeyDto.getId()).getBody();
        instance = Electrica.instance(new SingleInstanceHttpModule(invokerServiceUrl),
                fullAccessKeyDto.getKey());
    }

    @AfterClass
    public void close() {
        try {
            instance.close();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test(dependsOnGroups = {FILL_DATA_GROUP})
    public void testSlackV1() throws IntegrationException, IOException, TimeoutException {
        SlackChannelV1Manager channelManager = new SlackChannelV1Manager(instance);
        boolean result = false;
        result = channelManager.sendMessage(slackChannel, "This is Slack V1 test.");
        assertTrue(result);
    }


    @Test(dependsOnGroups = {FILL_DATA_GROUP})
    public void testSlackV1Broadcast() throws IntegrationException, IOException, TimeoutException {
        SlackChannelV1Manager channelManager = new SlackChannelV1Manager(instance);
        boolean result = false;
        result = channelManager.broadcast("This is Slack V1 Broadcast test.");
        assertTrue(result);
    }

    @Test(dependsOnGroups = {FILL_DATA_GROUP})
    public void testSlackV1SendMessageByConnectionName() throws IntegrationException, IOException, TimeoutException {
        Optional<ConnectorDto> slackConnector = connectorClient.findAll().getBody().stream()
                .filter(c -> c.getName().equalsIgnoreCase(SLACK_CHANNEL_V1)).findAny();
        assertTrue(slackConnector.isPresent());

        Optional<ConnectionDto> connectionDto = connectionClient.findAllByUser(user.getId(),
                slackConnector.get().getId()).getBody().stream().findAny();
        assertTrue(connectionDto.isPresent());

        SlackChannelV1Manager channelManager = new SlackChannelV1Manager(instance);

        boolean result = channelManager.sendMessageByConnectionName(connectionDto.get().getName(),
                "Slack V1 test send message by connection name");
        assertTrue(result);
    }

    @Test(dependsOnGroups = {FILL_DATA_GROUP})
    public void testSlackV1AsyncSendMessage() throws IOException, TimeoutException {
        SlackChannelV1Manager channelManager = new SlackChannelV1Manager(instance);
        SlackChannelV1 slackChannelV1 = channelManager.getChannelByName(slackChannel);
        AsyncResponseHandler<ChannelV1SendMessageResult> result = new AsyncResponseHandler<>();
        slackChannelV1.submitMessage("This is Slack V1 async test.", result);
        Boolean out = result.awaitResponse(60L, TimeUnit.SECONDS);
        assertTrue(out);
    }

    private static class AsyncResponseHandler<T> implements ResponseHandler<T> {

        private final BlockingQueue<Object[]> responseQueue = new ArrayBlockingQueue<>(1);

        @Override
        public void onResult(T v) {
            responseQueue.add(new Object[]{v});
        }

        @Override
        public void onError(IntegrationException e) {
            responseQueue.add(new Object[]{e});
        }

        @SneakyThrows
        private Boolean awaitResponse(Long timeout, TimeUnit unit) {
            Object[] responseContainer = responseQueue.poll(timeout, unit);
            if (responseContainer == null) {
                throw new TimeoutException();
            }
            Object o = responseContainer[0];
            Boolean result = false;
            if (o instanceof ChannelV1SendMessageResult) {
                result = ((ChannelV1SendMessageResult) o).isSuccessful();
            } else if (o instanceof IntegrationException) {
                throw (IntegrationException) o;
            }
            return result;
        }
    }

}

