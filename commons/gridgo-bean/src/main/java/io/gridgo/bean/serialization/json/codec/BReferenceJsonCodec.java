package io.gridgo.bean.serialization.json.codec;

import java.io.IOException;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;

import io.gridgo.bean.BReference;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@SuppressWarnings("rawtypes")
public class BReferenceJsonCodec implements JsonCodec<BReference> {

    @NonNull
    private final BElementJsonCodec compositeCodec;

    @Override
    public void write(JsonWriter writer, BReference value) {
        if (value == null || value.getReference() == null) {
            writer.writeNull();
            return;
        }

        writer.serializeObject(value.getReference());
    }

    @Override
    public BReference read(JsonReader reader) throws IOException {
        return null;
    }
}
