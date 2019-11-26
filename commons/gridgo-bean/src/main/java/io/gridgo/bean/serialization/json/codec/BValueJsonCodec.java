package io.gridgo.bean.serialization.json.codec;

import java.io.IOException;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.NumberConverter;
import com.dslplatform.json.ParsingException;

import io.gridgo.bean.BValue;
import io.gridgo.utils.ByteArrayUtils;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@SuppressWarnings("rawtypes")
public class BValueJsonCodec implements JsonCodec<BValue> {

    @Override
    public void write(JsonWriter writer, BValue value) {
        if (value == null || value.isNull()) {
            writer.writeNull();
            return;
        }

        switch (value.getType()) {
        case CHAR:
            writer.writeString(value.getString());
            break;
        case RAW:
            writer.writeString(ByteArrayUtils.toHex(value.getRaw(), "0x"));
            break;
        default:
            writer.serializeObject(value.getData());
            break;
        }
    }

    @Override
    public BValue read(JsonReader reader) throws IOException {
        return read(reader, true);
    }

    BValue read(JsonReader reader, boolean isBeginning) throws ParsingException, IOException {
        try {
            switch (reader.last()) {
            case 'n':
                if (!reader.wasNull())
                    throw reader.newParseErrorAt("Expecting 'null' for false constant", 0);
                return BValue.of(null);
            case 't':
                if (!reader.wasTrue())
                    throw reader.newParseErrorAt("Expecting 'true' for false constant", 0);
                return BValue.of(true);
            case 'f':
                if (!reader.wasFalse())
                    throw reader.newParseErrorAt("Expecting 'false' for false constant", 0);
                return BValue.of(false);
            case '"':
                return BValue.of(reader.readString());
            default:
                return BValue.of(NumberConverter.deserializeNumber(reader));
            }
        } catch (ParsingException e) {
            if (isBeginning)
                return BValue.of(reader.toString());
            throw e;
        }
    }
}
