package io.electrica.it.integration.slack;

import io.electrica.it.BaseIT;
import io.electrica.sdk.java.core.ResponseHandler;
import io.electrica.sdk.java.core.exception.IntegrationException;
import io.electrica.sdk.java.slack.channel.v2.SlackChannelV2;
import io.electrica.sdk.java.slack.channel.v2.SlackChannelV2Manager;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class SlackV2Test extends BaseIT {

    @BeforeEach
    public void setUp() {
        super.init();
    }

    @Test
    public void testSlackV2() throws IntegrationException, IOException, TimeoutException {
        SlackChannelV2Manager channelManager = new SlackChannelV2Manager(instance);
        channelManager.sendMessage(slackChannelV2, "This is Slack V2 test.");
    }

    @Test
    public void testSlackV2Broadcast() {
        SlackChannelV2Manager channelManager = new SlackChannelV2Manager(instance);
        assertTrue(channelManager.broadcast("This is Slack V2 Broadcast test."));
    }

    @Test
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
