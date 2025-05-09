package org.yangxc.operatoroverloading.core.handle.service;

import org.yangxc.operatoroverloading.core.exception.ElementException;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.*;

public class VariableSetContext {

    private final Map<String, VariableContext> varHap;

    public VariableSetContext() {
        this(new HashMap<>());
    }

    private VariableSetContext(Map<String, VariableContext> varHap) {
        this.varHap = varHap;
    }

    public void setup(VariableElement element, TypeElement typeElement) {
        try {
            String typeName = typeElement.getQualifiedName().toString();
            Set<Modifier> modifiers = element.getModifiers();
            if (!modifiers.contains(Modifier.PUBLIC)) {
                throw new ElementException(typeName + "." + element.getSimpleName() + "is not public", element);
            }
            if (!modifiers.contains(Modifier.STATIC)) {
                throw new ElementException(typeName + "." + element.getSimpleName() + "is not static", element);
            }
            add(VariableContext.createByStatic(element.asType().toString(), typeName, element.getSimpleName().toString()));
        } catch (ElementException e) {
            throw e;
        } catch (Exception e) {
            throw new ElementException(e, element);
        }
    }

    public void add(VariableContext variableContext) {
        varHap.put(variableContext.getQualifiedName(), variableContext);
    }

    public VariableContext get(String name) {
        return varHap.get(name);
    }

    public boolean contains(String name) {
        return varHap.containsKey(name);
    }

    public VariableSetContext copy() {
        return new VariableSetContext(new HashMap<>(varHap));
    }

    public List<VariableContext> values() {
        return new ArrayList<>(varHap.values());
    }

}
