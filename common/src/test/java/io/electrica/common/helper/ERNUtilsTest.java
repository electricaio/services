package io.electrica.common.helper;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class ERNUtilsTest {

    @Test
    public void testCreateERNWithResource() {
        Assert.assertEquals("stl://hackerrank:applications:1_0",
                ERNUtils.createERN("HackerRank", Optional.of("Applications"), "1.0")
        );
    }

    @Test
    public void testCreateERNWith2WordsResource() {
        Assert.assertEquals("stl://stripe:payments_api:1_0",
                ERNUtils.createERN("Stripe", Optional.of("Payments Api"), "1.0")
        );
    }

    @Test
    public void testCreateERNWithoutResource() {
        Assert.assertEquals("stl://mysql:5_6",
                ERNUtils.createERN("MySQL", "5.6")
        );

    }

    @Test
    public void testCreateERNPassingEmptyResource() {
        Assert.assertEquals("stl://mysql:5_6",
                ERNUtils.createERN("MySQL", Optional.empty(), "5.6")
        );
    }

    @Test
    public void testCreateERNContainingDotsOrColons() {
        Assert.assertEquals("stl://test_funky_stl_name:5_6_1",
                ERNUtils.createERN("Test.funky.STL Name", Optional.empty(), "5:6:1")
        );
    }

    @Test
    public void testEcapeInvalidChars() {
        Assert.assertEquals("test_funky_name",
                ERNUtils.escapeInvalidChars("test::funky name")
        );

        Assert.assertEquals("test_funky_name",
                ERNUtils.escapeInvalidChars("test:.:funky name")
        );

        Assert.assertEquals("test_funky_name",
                ERNUtils.escapeInvalidChars("test..funky :name")
        );
    }

}
