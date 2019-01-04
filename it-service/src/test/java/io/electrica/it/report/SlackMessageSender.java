package io.electrica.it.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.electrica.it.util.ReportContext;
import io.electrica.sdk.java.api.Connection;
import io.electrica.sdk.java.api.Connector;
import io.electrica.sdk.java.api.Electrica;
import io.electrica.sdk.java.core.SingleInstanceHttpModule;
import io.electrica.sdk.java.slack.channel.v1.SlackChannelV1;
import io.electrica.sdk.java.slack.channel.v1.SlackChannelV1Manager;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SlackMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlackMessageSender.class);
    private static final String GREEN_COLOR = "#36a64f";

    public void send(String payload) {
        ReportContext context = ReportContext.getInstance();

        try (Electrica instance = Electrica.instance(new SingleInstanceHttpModule(context.getInvokerServiceUrl()),
                context.getAccessKey())) {
            String message = payload;
            Connector connector = instance.connector(SlackChannelV1Manager.ERN);
            Connection connection = connector.connection(context.getSlackConnectionName());
            SlackChannelV1 channelV1 = new SlackChannelV1(connection);
            channelV1.send(message);
        } catch (Exception e) {
            LOGGER.error("Exception while sending message to slack. " + e.getMessage());
        }
    }

    private String getJsonForPayloadAsAttachment(String payload) throws JsonProcessingException {
        Attachment attachment = new Attachment();
        attachment.setColor(GREEN_COLOR);
        attachment.setText(payload);
        attachment.setTitle("Integration Test run on:" + new Date());
        SlackPayload slackPayload = new SlackPayload();
        slackPayload.getAttachments().add(attachment);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(slackPayload);
    }

    @Getter
    @Setter
    private class Attachment {
        private String color;
        private String pretext;
        private String title;
        private String text;
    }

    @Getter
    @Setter
    private class SlackPayload {
        private List<Attachment> attachments = new ArrayList<>();
    }
}

