package org.yangxc.core.handle.service;

import org.yangxc.core.constant.ClassName;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SymbolContext {

    private final Map<String, VariableContext> variableContexts;

    private final Map<String, String> imports;

    public SymbolContext() {
        this(List.of(), List.of());
    }

    public SymbolContext(List<VariableContext> variableContexts, List<String> imports) {
        this.variableContexts = variableContexts.stream().collect(Collectors.toMap(VariableContext::name, Function.identity()));
        this.imports = Stream.of(
                    imports.stream(),
                    ClassName.PRIMITIVE_NAME.stream()
                )
                .flatMap(Function.identity())
                .collect(Collectors.toMap(ClassName::getSimpleName, Function.identity()));
    }

    public void put(VariableContext variableContext) {
        variableContexts.put(variableContext.name(), variableContext);
    }

    public String getVarType(String varName) {
        return variableContexts.get(varName).type();
    }

    public boolean isVar(String name) {
        return variableContexts.containsKey(name);
    }

    public boolean isType(String name) {
        return imports.containsKey(name);
    }

    public String getType(String simpleType) {
        return imports.get(simpleType);
    }

}
