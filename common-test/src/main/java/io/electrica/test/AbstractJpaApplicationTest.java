package io.electrica.test;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ActiveProfiles("test")
public abstract class AbstractJpaApplicationTest extends AbstractTransactionalJUnit4SpringContextTests {

}
