package io.electrica.common.helper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SensitiveFieldUtilsTest {

    @Test
    public void toUpperCase() {
        assertNull(SensitiveFieldUtils.toUpperCase(null));
        assertEquals(SensitiveFieldUtils.toUpperCase("test"), "TEST");
    }

    @Test
    public void toUpperCaseMandatory() {
        assertEquals(SensitiveFieldUtils.toUpperCaseMandatory("test"), "TEST");
    }

    @Test(expected = IllegalArgumentException.class)
    public void toUpperCaseMandatoryFailed() {
        SensitiveFieldUtils.toUpperCaseMandatory(null);
    }

    @Test
    public void toLowerCase() {
        assertNull(SensitiveFieldUtils.toLowerCase(null));
        assertEquals(SensitiveFieldUtils.toLowerCase("TEST"), "test");
    }

    @Test
    public void toLowerCaseMandatory() {
        assertEquals(SensitiveFieldUtils.toLowerCaseMandatory("TEST"), "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void toLowerCaseMandatoryFailed() {
        SensitiveFieldUtils.toLowerCaseMandatory(null);
    }
}
