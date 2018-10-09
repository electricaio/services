package io.electrica.stl.util;

import org.springframework.test.context.ActiveProfilesResolver;

public class TestProfileResolver implements ActiveProfilesResolver {
    @Override
    public String[] resolve(Class<?> testClass) {
        // ensures that tests are running with 'test' profile
        return new String[] {"test"};
    }
}
