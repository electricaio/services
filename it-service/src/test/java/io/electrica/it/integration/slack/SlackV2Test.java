package io.electrica.it.integration.slack;

import io.electrica.it.BaseIT;
import io.electrica.sdk.java.core.Electrica;
import io.electrica.sdk.java.core.ResponseHandler;
import io.electrica.sdk.java.core.exception.IntegrationException;
import io.electrica.sdk.java.core.http.impl.SingleInstanceHttpModule;
import io.electrica.sdk.java.slack.channel.v2.SlackChannelV2;
import io.electrica.sdk.java.slack.channel.v2.SlackChannelV2Manager;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static io.electrica.it.util.ItServiceConstants.FILL_DATA_GROUP;
import static io.electrica.it.util.ItServiceConstants.TEST_GROUP;
import static org.testng.Assert.assertTrue;

public class SlackV2Test extends BaseIT {

    @Value("${it-service.invoker-service.url}")
    private String invokerServiceUrl;

    @Value("${it-service.slack.v2.channel}")
    private String slackChannel;

    private Electrica instance;

    @BeforeGroups(groups = {TEST_GROUP})
    public void init() {
        UserDto user = contextHolder.getUsers().get(0);
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
    public void testSlackV2() throws IntegrationException, IOException, TimeoutException {
        SlackChannelV2Manager channelManager = new SlackChannelV2Manager(instance);
        channelManager.sendMessage(slackChannel, "This is Slack V2 test.");
    }

    @Test(dependsOnGroups = {FILL_DATA_GROUP})
    public void testSlackV2Broadcast() {
        SlackChannelV2Manager channelManager = new SlackChannelV2Manager(instance);
        assertTrue(channelManager.broadcast("This is Slack V2 Broadcast test."));
    }

    @Test(dependsOnGroups = {FILL_DATA_GROUP})
    public void testSlackV2AsyncSendMessage() throws IOException {
        SlackChannelV2Manager channelManager = new SlackChannelV2Manager(instance);
        SlackChannelV2 slackChannelV2 = channelManager.slackChannels().get(0);
        TestVoidHandler voidHandler = new TestVoidHandler();
        slackChannelV2.submitMessage("This is Slack V2 async test.", voidHandler);
        voidHandler.awaitResponse(60L, TimeUnit.SECONDS);
    }

    private static class TestVoidHandler implements ResponseHandler.Void {
        private final CountDownLatch latch = new CountDownLatch(1);

        @Override
        public void onResult() {
            latch.countDown();
        }

        @Override
        public void onError(IntegrationException e) {
            Assert.fail("Test Failed due to exception:" + e.getMessage());
        }

        @SneakyThrows
        private void awaitResponse(Long timeout, TimeUnit unit) {
            boolean completed = latch.await(timeout, unit);
            assertTrue(completed);
        }
    }
}
