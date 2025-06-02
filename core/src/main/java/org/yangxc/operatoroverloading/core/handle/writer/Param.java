package org.yangxc.operatoroverloading.core.handle.writer;

import java.util.Objects;

public class Param {
    public final String type;
    public final String name;

    public Param(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Param param = (Param) object;
        return Objects.equals(type, param.type) && Objects.equals(name, param.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name);
    }

}
