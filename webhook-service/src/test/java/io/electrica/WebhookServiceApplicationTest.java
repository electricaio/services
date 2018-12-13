package io.electrica;

import io.electrica.common.jpa.test.AbstractJpaApplicationTest;
import io.electrica.common.mq.webhook.WebhookQueueDispatcher;
import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static io.electrica.common.mq.webhook.WebhookMessages.queueName;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = WebhookServiceApplication.class)
public abstract class WebhookServiceApplicationTest extends AbstractJpaApplicationTest {

    @Inject
    @MockBean
    private WebhookQueueDispatcher queueDispatcher;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        when(queueDispatcher.createQueueIfAbsent(anyLong(), anyLong(), anyLong())).thenAnswer(invocation -> queueName(
                invocation.getArgument(0),
                invocation.getArgument(1),
                invocation.getArgument(2)
        ));
    }

    public void flushAndClear() {
        em.flush();
        em.clear();
    }

}
