package io.electrica;

import io.electrica.common.jpa.test.AbstractJpaApplicationTest;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = WebhookServiceApplication.class)
public abstract class WebhookServiceApplicationTest extends AbstractJpaApplicationTest {

}
