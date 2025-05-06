package org.yangxc.operatoroverloading.core;

import org.yangxc.operatoroverloading.core.annotation.OperatorService;

import java.lang.reflect.Constructor;

public class Overloading {

    public static <T> T get(Class<T> service) throws ReflectiveOperationException {
        return get(service, Overloading.class.getClassLoader());
    }

    public static <T> T get(Class<T> service, ClassLoader classLoader) throws ReflectiveOperationException {
        Class<?> implClass = classLoader.loadClass(getName(service));
        Constructor<?> constructor = implClass.getConstructor();
        return (T) constructor.newInstance();
    }

    private static String getName(Class<?> service) {
        OperatorService operatorService = service.getAnnotation(OperatorService.class);
        if (operatorService == null || "".equals(operatorService.value())) {
            return service.getName() + "Impl";
        }
        return operatorService.value();
    }

}
