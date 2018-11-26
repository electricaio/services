package io.electrica.common.helper;

import org.junit.Assert;
import org.junit.Test;

public class CollectionUtilsTest {

    @Test
    public void testisEmpty() {
        Object obj1 = null, obj2 = null, obj3 = null;
        Assert.assertTrue(CollectionUtils.isEmpty(obj1, obj2, obj3));
        obj2 = new Object();
        Assert.assertFalse(CollectionUtils.isEmpty(obj1, obj2, obj3));
        obj3 = new Object();
        Assert.assertFalse(CollectionUtils.isEmpty(obj1, obj2, obj3));
        obj1 = new Object();
        Assert.assertFalse(CollectionUtils.isEmpty(obj1, obj2, obj3));
    }

    @Test
    public void testnullToFalse() {
        Boolean bool = null;
        Assert.assertFalse(CollectionUtils.nullToFalse(bool));
        bool = Boolean.TRUE;
        Assert.assertTrue(CollectionUtils.nullToFalse(bool));
        bool = Boolean.FALSE;
        Assert.assertFalse(CollectionUtils.nullToFalse(bool));
    }

    @Test
    public void testisNotEmpty() {
        Object obj1 = null, obj2 = null, obj3 = null;
        Assert.assertFalse(CollectionUtils.isNotEmpty(obj1, obj2, obj3));
        obj2 = new Object();
        Assert.assertFalse(CollectionUtils.isNotEmpty(obj1, obj2, obj3));
        obj3 = new Object();
        Assert.assertFalse(CollectionUtils.isNotEmpty(obj1, obj2, obj3));
        obj1 = new Object();
        Assert.assertTrue(CollectionUtils.isNotEmpty(obj1, obj2, obj3));
    }

    @Test
    public void testnullToZeroLong() {
        Long number = null;
        Assert.assertEquals(0L, CollectionUtils.nullToZero(number));
        number = 5L;
        Assert.assertEquals(5L, CollectionUtils.nullToZero(number));
    }

    @Test
    public void testnullToZeroInteger() {
        Integer number = null;
        Assert.assertEquals(0, CollectionUtils.nullToZero(number));
        number = 5;
        Assert.assertEquals(5, CollectionUtils.nullToZero(number));
    }
}
