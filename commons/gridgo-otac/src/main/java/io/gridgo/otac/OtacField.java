package io.gridgo.otac;

import java.util.HashSet;
import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class OtacField extends OtacNamedElement implements OtacRequireImports, OtacDeclaringClassAware {

    @Delegate(types = OtacDeclaringClassAware.class)
    private final OtacDeclaringClassAware declaringClassHolder = OtacDeclaringClassAware.newInstance();

    @Getter
    @Builder.Default
    private boolean isVolatile = false;

    @Getter
    @Builder.Default
    private boolean isTransient = false;

    @Getter
    private @NonNull OtacType type;

    @Getter
    private OtacValue initValue;

    @Getter
    @Builder.Default
    private boolean generateSetter = false;

    @Getter
    @Builder.Default
    private boolean generateGetter = false;

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append(super.toString()) //
                .append(isTransient() ? "transient " : "") //
                .append(isVolatile() ? "volatile " : "") //
                .append(type.toString()) //
                .append(getName()) //
                .append(initValue != null ? (" = " + initValue.toString().trim()) : "") //
                .append(";\n");
        return sb.toString();
    }

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        imports.addAll(type.requiredImports());
        if (initValue != null)
            imports.addAll(initValue.requiredImports());
        return imports;
    }
}
