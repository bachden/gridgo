package io.gridgo.utils.pojo.getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.utils.pojo.PojoFlattenIndicator;
import io.gridgo.utils.pojo.getter.fieldwalkers.ArrayFieldWalker;

public class TestArrayFieldWalker {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testWalkShallow() {
        var result = new HashMap<PojoFlattenIndicator, List>();
        var list = new Object[] {1, Map.of("k2", 2)};
        var fieldWalker = ArrayFieldWalker.getInstance();
        fieldWalker.walk(list, null, (indicator, value, signature, proxy) -> {
            result.computeIfAbsent(indicator, k -> new ArrayList<>())
                  .add(value);
        }, true);
        Assert.assertNull(result.get(PojoFlattenIndicator.START_MAP));
        Assert.assertNull(result.get(PojoFlattenIndicator.KEY));
        Assert.assertNull(result.get(PojoFlattenIndicator.END_MAP));
        Assert.assertEquals(List.of(2), result.get(PojoFlattenIndicator.START_ARRAY));
        Assert.assertEquals(List.of(2), result.get(PojoFlattenIndicator.END_ARRAY));
        var values = result.get(PojoFlattenIndicator.VALUE);
        Assert.assertEquals(Set.of(list), Set.copyOf(values));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testWalkDeep() {
        var result = new HashMap<PojoFlattenIndicator, List>();
        var list = new Object[] {1, Map.of("k2", 2)};
        var fieldWalker = ArrayFieldWalker.getInstance();
        fieldWalker.walk(list, null, (indicator, value, signature, proxy) -> {
            result.computeIfAbsent(indicator, k -> new ArrayList<>())
                  .add(value);
        }, false);
        Assert.assertEquals(List.of(1), result.get(PojoFlattenIndicator.START_MAP));
        Assert.assertEquals(List.of("k2"), result.get(PojoFlattenIndicator.KEY));
        Assert.assertEquals(List.of(1), result.get(PojoFlattenIndicator.END_MAP));
        Assert.assertEquals(List.of(2), result.get(PojoFlattenIndicator.START_ARRAY));
        Assert.assertEquals(List.of(2), result.get(PojoFlattenIndicator.END_ARRAY));
        var values = result.get(PojoFlattenIndicator.VALUE);
        Assert.assertEquals(Set.of(1, 2), Set.copyOf(values));
    }
}
