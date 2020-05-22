package io.gridgo.utils.pojo.getter;

import io.gridgo.utils.pojo.PojoFlattenIndicator;
import io.gridgo.utils.pojo.PojoMethodSignature;

@FunctionalInterface
public interface PojoFlattenAcceptor {

    void accept(PojoFlattenIndicator indicator, Object value, PojoMethodSignature signature, PojoGetterProxy proxy);
}
