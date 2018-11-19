package io.electrica.common.helper;

import org.junit.Assert;
import org.junit.Test;

public class ERNUtilsTest {

    @Test
    public void testCreateERNWithResource() {
        Assert.assertEquals("ern://hackerrank:applications:1_0",
                ERNUtils.createERN("HackerRank", "Applications", "1.0")
        );
    }

    @Test
    public void testCreateERNWith2WordsResource() {
        Assert.assertEquals("ern://stripe:payments_api:1_0",
                ERNUtils.createERN("Stripe", "Payments Api", "1.0")
        );
    }

    @Test
    public void testCreateERNWithoutResource() {
        Assert.assertEquals("ern://mysql:5_6",
                ERNUtils.createERN("MySQL", "5.6")
        );

    }

    @Test
    public void testCreateERNPassingEmptyResource() {
        Assert.assertEquals("ern://mysql:5_6",
                ERNUtils.createERN("MySQL", "5.6")
        );
    }

    @Test
    public void testCreateERNContainingDotsOrColons() {
        Assert.assertEquals("ern://test_funky_stl_name:5_6_1",
                ERNUtils.createERN("Test.funky.STL Name", "5:6:1")
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
