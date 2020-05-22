package io.gridgo.utils.pojo.setter;

import static io.gridgo.utils.StringUtils.lowerCaseFirstLetter;
import static java.lang.reflect.Modifier.isPublic;

import java.lang.reflect.Method;
import java.util.Set;

import io.gridgo.utils.pojo.AbstractMethodSignatureExtractor;
import io.gridgo.utils.pojo.IgnoreDefaultTranslator;
import io.gridgo.utils.pojo.MethodSignatureExtractor;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.translator.OnSetTranslate;
import io.gridgo.utils.pojo.translator.ValueTranslator;
import io.gridgo.utils.pojo.translator.ValueTranslators;
import lombok.NonNull;

public class SetterMethodSignatureExtractor extends AbstractMethodSignatureExtractor {

    private static final String SETTER_PREFIX = "set";

    private static final MethodSignatureExtractor INSTANCE = new SetterMethodSignatureExtractor();

    public static final MethodSignatureExtractor getInstance() {
        return INSTANCE;
    }

    @Override
    protected boolean isApplicable(@NonNull Method method) {
        var methodName = method.getName();
        return method.getParameterCount() == 1 //
                && isPublic(method.getModifiers()) //
                && method.getReturnType() == Void.TYPE //
                && methodName.startsWith(SETTER_PREFIX) && methodName.length() > 3;
    }

    @Override
    protected String extractFieldName(String methodName) {
        return lowerCaseFirstLetter(methodName.substring(3));
    }

    @Override
    protected PojoMethodSignature extract(Method method, String fieldName, String transformRule,
            Set<String> ignoredFields) {

        var param = method.getParameters()[0];
        var signatureType = param.getType();

        var transformedFieldName = transformFieldName(method, fieldName, transformRule, ignoredFields, signatureType);

        return PojoMethodSignature.builder() //
                .method(method) //
                .fieldType(signatureType) //
                .fieldName(fieldName) //
                .transformedFieldName(transformedFieldName) //
                .valueTranslator(extractValueTranslator(method, fieldName)) //
                .build();
    }

    @SuppressWarnings("rawtypes")
    private ValueTranslator extractValueTranslator(Method method, String fieldName) {
        var annotationType = OnSetTranslate.class;
        if (method.isAnnotationPresent(annotationType)) {
            var key = method.getAnnotation(annotationType).value();
            return ValueTranslators.getInstance().lookupMandatory(key);
        }

        var field = getCorespondingField(method, fieldName);
        if (field != null && field.isAnnotationPresent(annotationType)) {
            var key = field.getAnnotation(annotationType).value();
            return ValueTranslators.getInstance().lookupMandatory(key);
        }

        var isIgnoreDefault = method.isAnnotationPresent(IgnoreDefaultTranslator.class) //
                || method.getDeclaringClass().isAnnotationPresent(IgnoreDefaultTranslator.class) //
                || (field != null && field.isAnnotationPresent(IgnoreDefaultTranslator.class));

        if (!isIgnoreDefault)
            return ValueTranslators.getInstance().lookupSetterDefault(method.getParameters()[0].getType());

        return null;
    }
}
