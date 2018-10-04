package io.electrica.common.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletException;
import java.io.IOException;

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
     * Execute some work with specified identity context.
     */
    public void executeWithContext(Identity identity, ContextWork work) throws IOException, ServletException {
        MDC.put("user_id", String.valueOf(identity.getUserId()));
        Identity old = threadLocal.get();
        if (old != null) {
            log.error("Identity context re-entrance!!! Was: {} New: {}", old, identity);
        }
        threadLocal.set(identity);
        try {
            work.doWork();
        } finally {
            MDC.remove("user_id");
            threadLocal.remove();
        }
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