package io.gridgo.utils.format;

import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import io.gridgo.utils.PrimitiveUtils;

public class CommonTextTransformerRegistry extends DefaultFormatTransformerRegistry {

    public static final FormatTransformer TO_STRING = (source) -> source == null ? null
            : PrimitiveUtils.getStringValueFrom(source);

    public static final FormatTransformer CAPITALIZE = (
            source) -> source instanceof String ? WordUtils.capitalize((String) source) : source;

    public static final FormatTransformer UPPER_CASE = source -> source instanceof String
            ? source.toString().toUpperCase()
            : source;

    public static final FormatTransformer LOWER_CASE = source -> source instanceof String
            ? source.toString().toLowerCase()
            : source;

    public static final FormatTransformer TRIM = source -> source instanceof String ? source.toString().trim() : source;

    public static final FormatTransformer STRIP_ACCENTS = source -> source instanceof String
            ? StringUtils.stripAccents((String) source)
            : source;

    public static final FormatTransformer CAMEL_TO_SNAKE_CASE = source -> UPPER_CAMEL.to(LOWER_UNDERSCORE, (String) source);
    public static final FormatTransformer SNAKE_TO_CAMEL_CASE = source -> LOWER_UNDERSCORE.to(UPPER_CAMEL, (String) source);

    public static final CommonTextTransformerRegistry newInstance() {
        return new CommonTextTransformerRegistry();
    }

    private CommonTextTransformerRegistry() {
        this.addTransformer("trim", TRIM);
        this.addTransformer("toString", TO_STRING);
        this.addTransformer("upperCase", UPPER_CASE);
        this.addTransformer("lowerCase", LOWER_CASE);
        this.addTransformer("capitalize", CAPITALIZE);
        this.addTransformer("stripAccents", STRIP_ACCENTS);
        this.addTransformer("camelToSnake", CAMEL_TO_SNAKE_CASE);
        this.addTransformer("snakeToCamel", SNAKE_TO_CAMEL_CASE);
    }
}
