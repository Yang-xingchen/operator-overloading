package org.yangxc.operatoroverloading.core.handle.service;

import org.yangxc.operatoroverloading.core.constant.ClassName;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SymbolContext {

    private final VariableSetContext variableContexts;

    private final Map<String, String> imports;

    public SymbolContext() {
        this(new VariableSetContext(), List.of());
    }

    public SymbolContext(VariableSetContext variableContexts, List<String> imports) {
        this.variableContexts = variableContexts;
        this.imports = Stream.of(
                    imports.stream(),
                    ClassName.PRIMITIVE_NAME.stream()
                )
                .flatMap(Function.identity())
                .collect(Collectors.toMap(ClassName::getSimpleName, Function.identity()));
    }

    public boolean isVar(String type, String name) {
        return variableContexts.contains(type + "." + name);
    }

    public boolean isVar(String name) {
        return variableContexts.contains(name);
    }

    public boolean isType(String name) {
        return imports.containsKey(name);
    }

    public String getType(String type) {
        return imports.getOrDefault(type, type);
    }

}
