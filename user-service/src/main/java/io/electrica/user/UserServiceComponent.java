package io.electrica.user;

import io.electrica.common.SomeCommonComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Component
public class UserServiceComponent {

    private final Logger logger = LoggerFactory.getLogger(UserServiceComponent.class);

    private final SomeCommonComponent someCommonComponent;

    @Inject
    public UserServiceComponent(SomeCommonComponent someCommonComponent) {
        this.someCommonComponent = someCommonComponent;
    }

    @PostConstruct
    public void sayHello(){
        logger.info(someCommonComponent.sayHello());
    }
}
