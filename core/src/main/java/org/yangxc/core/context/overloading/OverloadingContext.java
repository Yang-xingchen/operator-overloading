package org.yangxc.core.context.overloading;

import org.yangxc.core.constant.ClassName;

import java.util.HashMap;
import java.util.Map;

public class OverloadingContext {

    private final Map<String, ClassOverloadingContext> map;

    public OverloadingContext() {
        map = new HashMap<>();
        map.put(ClassName.BIG_DECIMAL, ClassOverloadingContext.BIG_DECIMAL_CONTEXT);
    }

    public ClassOverloadingContext get(String type) {
        return map.get(type);
    }

    public void put(String type, OperatorOverloadingContext context) {
        map.computeIfAbsent(type, ClassOverloadingContext::new).set(context);
    }

}
