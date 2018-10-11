package io.electrica.stl.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class ERNUtilsTest {

    @Test
    public void testCreateERNWithResource() {
        Assert.assertEquals("stl://hackerrank:applications:1.0",
                ERNUtils.createERN("HackerRank", Optional.of("Applications"), "1.0")
        );
    }

    @Test
    public void testCreateERNWith2WordsResource() {
        Assert.assertEquals("stl://stripe:payments_api:1.0",
                ERNUtils.createERN("Stripe", Optional.of("Payments Api"), "1.0")
        );
    }

    @Test
    public void testCreateERNWithoutResource() {
        Assert.assertEquals("stl://mysql:5.6",
                ERNUtils.createERN("MySQL", "5.6")
        );

    }

    @Test
    public void testCreateERNPassingEmptyResource() {
        Assert.assertEquals("stl://mysql:5.6",
                ERNUtils.createERN("MySQL", Optional.empty(), "5.6")
        );
    }

}
