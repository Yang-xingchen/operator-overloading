package org.yangxc.operatoroverloading.core.handle.overloading;

import org.yangxc.operatoroverloading.core.constant.CastMethodType;

import java.util.Objects;

public final class CastContext {

    private final String from;
    private final CastMethodType type;
    private final String className;
    private final String name;
    private final String to;

    public CastContext(String from, CastMethodType type, String className, String name, String to) {
        this.from = from;
        this.type = type;
        this.className = className;
        this.name = name;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public CastMethodType getType() {
        return type;
    }

    public String getClassName() {
        return className;
    }

    public String getName() {
        return name;
    }

    public String getTo() {
        return to;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (CastContext) obj;
        return Objects.equals(this.from, that.from) &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.className, that.className) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, type, className, name, to);
    }

    @Override
    public String toString() {
        return "CastContext[" +
                "from=" + from + ", " +
                "type=" + type + ", " +
                "className=" + className + ", " +
                "name=" + name + ", " +
                "to=" + to + ']';
    }

}
