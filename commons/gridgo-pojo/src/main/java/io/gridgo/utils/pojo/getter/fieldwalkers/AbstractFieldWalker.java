package io.gridgo.utils.pojo.getter.fieldwalkers;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import static io.gridgo.utils.PrimitiveUtils.isPrimitive;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.VALUE;

import io.gridgo.utils.pojo.getter.PojoFlattenAcceptor;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.getter.PojoGetterRegistry;

public abstract class AbstractFieldWalker implements FieldWalker {

    private static final FieldWalker arrayFieldWalker = ArrayFieldWalker.getInstance();

    private static final FieldWalker collectionFieldWalker = CollectionFieldWalker.getInstance();

    private static final FieldWalker mapFieldWalker = MapFieldWalker.getInstance();

    private static final FieldWalker pojoFieldWalker = PojoFieldWalker.getInstance();

    protected void walkRecursive(Object target, PojoGetterProxy proxy, PojoFlattenAcceptor walker, boolean shallowly) {
        Class<?> type;

        if (target == null //
                || isPrimitive(type = target.getClass()) //
                || type == Date.class //
                || type == java.sql.Date.class) {

            walker.accept(VALUE, target, null, null);
            return;
        }

        if (type.isArray()) {
            arrayFieldWalker.walk(target, proxy, walker, shallowly);
            return;
        }

        if (Collection.class.isInstance(target)) {
            collectionFieldWalker.walk(target, proxy, walker, shallowly);
            return;
        }

        if (Map.class.isInstance(target)) {
            mapFieldWalker.walk(target, proxy, walker, shallowly);
            return;
        }

        var _proxy = proxy != null ? proxy : PojoGetterRegistry.DEFAULT.getGetterProxy(type);
        pojoFieldWalker.walk(target, _proxy, walker, shallowly);
    }
}
