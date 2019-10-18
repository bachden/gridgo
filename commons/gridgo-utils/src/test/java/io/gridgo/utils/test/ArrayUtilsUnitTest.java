package io.gridgo.utils.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import io.gridgo.utils.ArrayUtils;

public class ArrayUtilsUnitTest {

    @Test
    public void testIsArray() {
        Assert.assertTrue(ArrayUtils.isArrayOrCollection(Byte[].class));
        Assert.assertTrue(ArrayUtils.isArrayOrCollection(String[].class));
        Assert.assertTrue(ArrayUtils.isArrayOrCollection(Object[].class));
        Assert.assertTrue(ArrayUtils.isArrayOrCollection(Collection.class));
        Assert.assertTrue(ArrayUtils.isArrayOrCollection(new ArrayList<String>().getClass()));
        Assert.assertFalse(ArrayUtils.isArrayOrCollection(ArrayUtils.class));
    }

    @Test
    public void testForEachInteger() {
        var atomic = new AtomicInteger(0);
        var list = new Integer[] { 1, 2, 3, 4 };
        ArrayUtils.<Integer>foreach(list, e -> atomic.addAndGet(e));
        Assert.assertEquals(10, atomic.get());
    }

    @Test
    public void testForEachInt() {
        var atomic = new AtomicInteger(0);
        var list = new int[] { 1, 2, 3, 4 };
        ArrayUtils.<Integer>foreach(list, e -> atomic.addAndGet(e));
        Assert.assertEquals(10, atomic.get());
    }

    @Test
    public void testForEachBool() {
        var trueCounter = new AtomicInteger(0);
        var falseCounter = new AtomicInteger(0);
        var list = new boolean[] { true, false, true, false };
        ArrayUtils.<Boolean>foreach(list, e -> {
            if (e)
                trueCounter.addAndGet(1);
            else
                falseCounter.addAndGet(1);
        });
        Assert.assertEquals(2, trueCounter.get());
        Assert.assertEquals(2, falseCounter.get());
    }

    @Test
    public void testLength() {
        var list = new Integer[] { 1, 2, 3, 4 };
        Assert.assertEquals(4, ArrayUtils.length(list));
        var list2 = Arrays.asList(new Integer[] { 1, 2, 3, 4 });
        Assert.assertEquals(4, ArrayUtils.length(list2));
        var map = Collections.singletonMap("k", 1);
        Assert.assertEquals(-1, ArrayUtils.length(map));
    }

    @Test
    public void testToArray() {
        var arr = new Integer[] { 1, 2, 3, 4 };
        var list2 = Arrays.asList(new Integer[] { 1, 2, 3, 4 });
        Assert.assertArrayEquals(arr, ArrayUtils.toArray(Integer.class, list2));
    }

    @Test
    public void testToString() {
        var arr1 = new Integer[] { 1, 2, 3, 4 };
        Assert.assertEquals("1, 2, 3, 4", ArrayUtils.toString(arr1));
        var arr2 = new int[] { 1, 2, 3, 4 };
        Assert.assertEquals("1, 2, 3, 4", ArrayUtils.toString(arr2));
        var arr3 = new boolean[] { true, false, true, false };
        Assert.assertEquals("true, false, true, false", ArrayUtils.toString(arr3));
    }
}
