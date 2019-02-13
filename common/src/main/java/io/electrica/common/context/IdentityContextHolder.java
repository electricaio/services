package io.electrica.common.context;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Service that manage {@link Identity} contexts by threads.
 */
public class IdentityContextHolder {

    private static final Logger log = LoggerFactory.getLogger(IdentityContextHolder.class);
    private static final IdentityContextHolder INSTANCE = new IdentityContextHolder();
    private final ThreadLocal<Identity> threadLocal = new ThreadLocal<>();

    /**
     * @return static singleton instance of {@link IdentityContextHolder}
     */
    public static IdentityContextHolder getInstance() {
        return INSTANCE;
    }

    /**
     * Allow to get {@link Identity} associated with current thread.
     *
     * @return identity
     */
    public Identity getIdentity() {
        return threadLocal.get();
    }

    /**
     * Set specified identity to thread-local context.
     */
    @VisibleForTesting
    public void setIdentity(Identity identity) {
        MDC.put("organization_id", String.valueOf(identity.getOrganizationId()));
        MDC.put("user_id", String.valueOf(identity.getUserId()));
        MDC.put("access_key_id", String.valueOf(identity.getAccessKeyIdIfPresent()));
        Identity old = threadLocal.get();
        if (old != null) {
            log.error("Identity context re-entrance!!! Was: {} New: {}", old, identity);
        }
        threadLocal.set(identity);
    }

    /**
     * Execute some work with specified identity context.
     */
    public void executeWithContext(Identity identity, ContextWork work) throws IOException, ServletException {
        setIdentity(identity);
        try {
            work.doWork();
        } finally {
            clearIdentity();
        }
    }

    public void logForUser(Long organizationId, Long userId, Long accessKeyId, Runnable logRunnable) {
        MDC.put("organization_id", String.valueOf(organizationId));
        MDC.put("user_id", String.valueOf(userId));
        MDC.put("access_key_id", String.valueOf(accessKeyId));

        logRunnable.run();

        Identity identity = threadLocal.get();
        if (identity != null) {
            MDC.put("organization_id", String.valueOf(identity.getOrganizationId()));
            MDC.put("user_id", String.valueOf(identity.getUserId()));
            MDC.put("access_key_id", String.valueOf(identity.getAccessKeyIdIfPresent()));
        }
    }

    /**
     * Clean up context for current thread.
     */
    @VisibleForTesting
    public void clearIdentity() {
        MDC.remove("organization_id");
        MDC.remove("user_id");
        MDC.remove("access_key_id");
        threadLocal.remove();
    }

    /**
     * Describe some servlet specific work.
     */
    public interface ContextWork {
        void doWork() throws IOException, ServletException;
    }

    /**
     * Adds instance of {@link IdentityContextHolder} to container.
     */
    @Configuration
    public static class IdentityContextHolderConfigurer {
        @Bean
        public IdentityContextHolder identityContextHolder() {
            return getInstance();
        }
    }
}
