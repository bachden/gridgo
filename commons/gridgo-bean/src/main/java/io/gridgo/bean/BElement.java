package io.gridgo.bean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Function;

import io.gridgo.bean.factory.BFactory;
import io.gridgo.bean.serialization.BSerializerRegistryAware;
import io.gridgo.utils.wrapper.ByteBufferInputStream;
import lombok.NonNull;

public interface BElement extends BSerializerRegistryAware, BJsonSupport, BBytesSupport {

    static <T extends BElement> T wrapAny(Object data) {
        return BFactory.DEFAULT.wrap(data);
    }

    static <T extends BElement> T ofAny(Object data) {
        return BFactory.DEFAULT.fromAny(data);
    }

    ////////////////// JSON Support//////////////////
    static <T extends BElement> T ofJson(String json) {
        return ofBytes(json.getBytes(StandardCharsets.UTF_8), JSON_SERIALIZER_NAME);
    }

    static <T extends BElement> T ofJson(InputStream inputStream) {
        return ofBytes(inputStream, JSON_SERIALIZER_NAME);
    }

    ///////////////// Bytes Support////////////////////
    static <T extends BElement> T ofBytes(@NonNull InputStream in, String serializerName) {
        return BFactory.DEFAULT.fromBytes(in, serializerName);
    }

    static <T extends BElement> T ofBytes(@NonNull ByteBuffer buffer, String serializerName) {
        return ofBytes(new ByteBufferInputStream(buffer), serializerName);
    }

    static <T extends BElement> T ofBytes(@NonNull byte[] bytes, String serializerName) {
        return ofBytes(new ByteArrayInputStream(bytes), serializerName);
    }

    static <T extends BElement> T ofBytes(@NonNull InputStream in) {
        return ofBytes(in, null);
    }

    static <T extends BElement> T ofBytes(@NonNull ByteBuffer buffer) {
        return ofBytes(buffer, null);
    }

    static <T extends BElement> T ofBytes(@NonNull byte[] bytes) {
        return ofBytes(bytes, null);
    }

    default boolean isContainer() {
        return false;
    }

    default boolean isNullValue() {
        return this.isValue() && this.asValue().isNull();
    }

    default boolean isArray() {
        return false;
    }

    default <T> T isArrayThen(Function<BArray, T> handler) {
        if (this.isArray()) {
            return handler.apply(this.asArray());
        }
        return null;
    }

    default void isArrayThen(Consumer<BArray> handler) {
        if (this.isArray()) {
            handler.accept(this.asArray());
        }
    }

    default boolean isObject() {
        return false;
    }

    default <T> T isObjectThen(Function<BObject, T> handler) {
        if (this.isObject()) {
            return handler.apply(this.asObject());
        }
        return null;
    }

    default void isObjectThen(Consumer<BObject> handler) {
        if (this.isObject()) {
            handler.accept(this.asObject());
        }
    }

    default boolean isValue() {
        return false;
    }

    default <T> T isValueThen(Function<BValue, T> handler) {
        if (this.isValue()) {
            return handler.apply(this.asValue());
        }
        return null;
    }

    default void isValueThen(Consumer<BValue> handler) {
        if (this.isValue()) {
            handler.accept(this.asValue());
        }
    }

    default boolean isReference() {
        return false;
    }

    default <T> T isReferenceThen(Function<BReference, T> handler) {
        if (this.isReference()) {
            return handler.apply(this.asReference());
        }
        return null;
    }

    default void isReferenceThen(Consumer<BReference> handler) {
        if (this.isReference()) {
            handler.accept(this.asReference());
        }
    }

    default BContainer asContainer() {
        return (BContainer) this;
    }

    default BObject asObject() {
        return (BObject) this;
    }

    default BArray asArray() {
        return (BArray) this;
    }

    default BValue asValue() {
        return (BValue) this;
    }

    default BReference asReference() {
        return (BReference) this;
    }

    BType getType();

    <T extends BElement> T deepClone();

    <T> T getInnerValue();
}
