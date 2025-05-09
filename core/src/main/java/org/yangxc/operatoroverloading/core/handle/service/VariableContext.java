package org.yangxc.operatoroverloading.core.handle.service;

import org.yangxc.operatoroverloading.core.constant.VariableDefineType;

import java.util.Objects;

public final class VariableContext {

    private final VariableDefineType defineType;
    private final String name;
    private final String type;
    private final String defineTypeName;
    private final int statement;

    private VariableContext(VariableDefineType defineType, String type, String name, String defineTypeName, int statement) {
        this.defineType = defineType;
        this.name = name;
        this.type = type;
        this.defineTypeName = defineTypeName;
        this.statement = statement;
    }

    public static VariableContext createByParam(String type, String name) {
        return new VariableContext(VariableDefineType.PARAM, type, name, null, -1);
    }

    public static VariableContext createByLocal(String type, String name, int statement) {
        return new VariableContext(VariableDefineType.LOCAL, type, name, null, statement);
    }

    public static VariableContext createByThis(String type, String name) {
        return new VariableContext(VariableDefineType.THIS, type, name, null, -1);
    }

    public static VariableContext createByStatic(String type, String defineTypeName, String name) {
        return new VariableContext(VariableDefineType.STATIC, type, name, defineTypeName, -1);
    }

    public VariableDefineType getDefineType() {
        return defineType;
    }

    public String getName() {
        return name;
    }

    public String getQualifiedName() {
        return switch (defineType) {
            case PARAM, LOCAL -> name;
            case THIS -> "this." + name;
            case STATIC -> defineTypeName + "." + name;
        };
    }

    public String getType() {
        return type;
    }

    public String getDefineTypeName() {
        return defineTypeName;
    }

    public int getStatement() {
        return statement;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (VariableContext) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.type, that.type) &&
                this.statement == that.statement;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, statement);
    }

    @Override
    public String toString() {
        return "(" + type + ")" + name;
    }

}
