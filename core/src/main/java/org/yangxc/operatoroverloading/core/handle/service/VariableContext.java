package org.yangxc.operatoroverloading.core.handle.service;

import java.util.Objects;

public final class VariableContext {

    private final String name;
    private final String type;
    private final int statement;

    public VariableContext(String name, String type, int statement) {
        this.name = name;
        this.type = type;
        this.statement = statement;
    }

    public String name() {
        return name;
    }

    public String type() {
        return type;
    }

    public int statement() {
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
        return "VariableContext[" +
                "name=" + name + ", " +
                "type=" + type + ", " +
                "statement=" + statement + ']';
    }

}
