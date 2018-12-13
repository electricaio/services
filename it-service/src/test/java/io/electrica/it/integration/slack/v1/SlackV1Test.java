package io.electrica.it.integration.slack.v1;

import io.electrica.it.BaseIT;
import io.electrica.sdk.java.core.Electrica;
import io.electrica.sdk.java.core.exception.IntegrationException;
import io.electrica.sdk.java.core.http.impl.SingleInstanceHttpModule;
import io.electrica.sdk.java.slack.channel.v1.SlackChannelV1Manager;
import io.electrica.user.dto.AccessKeyDto;
import io.electrica.user.dto.FullAccessKeyDto;
import io.electrica.user.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static io.electrica.it.util.ItServiceConstants.FILL_DATA_GROUP;
import static org.testng.Assert.assertTrue;

public class SlackV1Test extends BaseIT {

    @Value("${it-service.invoker-service.url}")
    private String invokerServiceUrl;

    @Value("${it-service.slack.v1.channel}")
    private String slackChannel;

    @Test(dependsOnGroups = {FILL_DATA_GROUP})
    public void testSlackV1() throws IntegrationException, IOException, TimeoutException {

        UserDto user = contextHolder.getUsers().get(0);
        contextHolder.setContextForUser(user.getEmail());
        AccessKeyDto accessKeyDto = accessKeyClient.findAllNonArchivedByUser(user.getId()).getBody().get(0);
        FullAccessKeyDto fullAccessKeyDto = accessKeyClient.getAccessKey(accessKeyDto.getId()).getBody();

        Electrica instance = Electrica.instance(new SingleInstanceHttpModule(invokerServiceUrl),
                fullAccessKeyDto.getKey());
        SlackChannelV1Manager channelManager = new SlackChannelV1Manager(instance);
        boolean result = channelManager.sendMessage(slackChannel, "Hi.. This is Integration Service Test");
        assertTrue(result);

    }
}

