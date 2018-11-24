package io.electrica;

import io.electrica.common.jpa.test.AbstractJpaApplicationTest;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@SpringBootTest(classes = WebhookServiceApplication.class)
public class WebhookServiceApplicationTest extends AbstractJpaApplicationTest {

    @PersistenceContext
    private EntityManager em;

    public void flushAndClear() {
        em.flush();
        em.clear();
    }
}
