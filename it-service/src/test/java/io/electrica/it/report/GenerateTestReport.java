package io.electrica.it.report;

import io.electrica.it.BaseIT;
import io.electrica.it.util.ReportContext;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.stream.Collectors.joining;


public class GenerateTestReport implements BeforeAllCallback, AfterAllCallback, AfterTestExecutionCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateTestReport.class);
    private TestExecutionSummary summary;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        summary = new TestExecutionSummary();
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        if (context.getExecutionException().isPresent()) {
            summary.testsFailed.incrementAndGet();
            summary.addToFailureList(context.getTestMethod().get().getName());
        } else {
            summary.testsSucceeded.incrementAndGet();
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        String payload = generatePayload();
        publishReport(payload);
        BaseIT.close();
    }

    private void publishReport(String payload) {
        LOGGER.info("-->" + payload);
        if (ReportContext.getInstance().getPublishReport()) {
            SlackMessageSender slackMessageSender = new SlackMessageSender();
            slackMessageSender.send(payload);
        }
    }

    private String generatePayload() {
        Long timeTaken = (System.currentTimeMillis() - summary.getTimeStarted()) / 60;
        StringBuilder stringBuilder = new StringBuilder();
        String testStatus = summary.getTestsFailed().get() > 0 ? "Failure" : "Success";
        stringBuilder.append("******Integration Test run on: " + new Date() + " ******\n")
                .append("Test status: " + testStatus + " ,Time Taken:" + timeTaken + " sec.\n")
                .append("Passed:" + summary.getTestsSucceeded())
                .append(" ,Failed:" + summary.getTestsFailed())
                .append(getTestResultDetails() + "\n")
                .append("**************End Of Message**************");
        return stringBuilder.toString();
    }

    private String getTestResultDetails() {
        String out = "";
        if (summary.testsFailed.get() > 0) {
            StringBuilder str = new StringBuilder();
            str.append("\nFailed Tests")
                    .append("\n--------------\n")
                    .append(summary.failures.stream()
                            .collect(joining("\n")));
            out = str.toString();
        }
        return out;
    }

    @Setter
    @Getter
    private class TestExecutionSummary {

        private final AtomicLong testsSucceeded = new AtomicLong();
        private final AtomicLong testsFailed = new AtomicLong();
        private final long timeStarted;

        private final List<String> failures = new ArrayList<>();

        public TestExecutionSummary() {
            this.timeStarted = System.currentTimeMillis();
        }

        public void addToFailureList(String testName) {
            failures.add(testName);
        }
    }

}
