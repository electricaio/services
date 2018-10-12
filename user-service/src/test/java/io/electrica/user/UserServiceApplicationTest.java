package io.electrica.user;

import io.electrica.UserServiceApplication;
import io.electrica.test.AbstractJpaApplicationTest;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = UserServiceApplication.class)
public abstract class UserServiceApplicationTest extends AbstractJpaApplicationTest {
}
