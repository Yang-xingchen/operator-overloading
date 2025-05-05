package org.yangxc.core.handle.overloading;

import org.yangxc.core.constant.ClassOverloading;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OverloadingContext {

    private final Map<String, ClassOverloadingContext> map;

    public OverloadingContext() {
        map = new HashMap<>(ClassOverloading.DEFAULT_CLASS_OVERLOADING);
    }

    public ClassOverloadingContext get(String type) {
        return map.get(type);
    }

    public void put(String type, OperatorOverloadingContext context) {
        map.computeIfAbsent(type, ClassOverloadingContext::new).set(context);
    }

    public Set<String> typeSet() {
        return map.keySet();
    }

}
