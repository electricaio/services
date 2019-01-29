package io.electrica.it.rule;

import io.electrica.common.rest.PathConstants;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class ServiceAwaitExtension implements BeforeAllCallback, AfterAllCallback {

    private static final long DEFAULT_CHECK_INTERVAL = TimeUnit.SECONDS.toMillis(10);
    private static final long DEFAULT_TIMEOUT = TimeUnit.MINUTES.toMillis(5);
    private static final List<String> DEFAULT_HOSTS = Arrays.asList(
            "http://localhost:22022",
            "http://localhost:22023",
            "http://localhost:22024",
            "http://localhost:22025",
            "http://localhost:22026",
            "http://localhost:22027",
            "http://localhost:22028"
    );

    private static final Waiter INSTANCE = new Waiter();

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        INSTANCE.beforeAll();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        INSTANCE.afterAll();
    }

    private static class Waiter {

        private final OkHttpClient httpClient;
        private final long checkoutInterval;
        private final long timeout;
        private final List<String> hosts;

        private boolean initialized = false;

        private Waiter() {
            this(DEFAULT_CHECK_INTERVAL, DEFAULT_TIMEOUT, DEFAULT_HOSTS);
        }

        private Waiter(long checkoutInterval, long timeout, List<String> hosts) {
            this.httpClient = new OkHttpClient();
            this.checkoutInterval = checkoutInterval;
            this.timeout = timeout;
            this.hosts = hosts;
        }

        private synchronized void beforeAll() throws Exception {
            if (!initialized) {
                for (String host : hosts) {
                    String serviceHealthUrl = host + PathConstants.HEALTH_PATH;
                    waitForService(serviceHealthUrl);
                }
                initialized = true;
            }
        }

        private void afterAll() {
            httpClient.dispatcher().cancelAll();
        }

        private void waitForService(String serviceHealthUrl) throws Exception {
            log.info("Waiting for container: " + serviceHealthUrl);
            long start = System.currentTimeMillis();
            Request request = new Request.Builder().url(serviceHealthUrl).get().build();

            boolean successful = false;
            while (!successful && (System.currentTimeMillis() - start) < timeout) {
                try (Response response = httpClient.newCall(request).execute()) {
                    successful = response.isSuccessful();
                } catch (IOException e) {
                    // design decision
                }
                if (!successful) {
                    Thread.sleep(checkoutInterval);
                }
            }
            if (!successful) {
                throw new TimeoutException("Container timed out: " + serviceHealthUrl);
            }
        }
    }
}
