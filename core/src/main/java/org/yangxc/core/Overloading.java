package org.yangxc.core;

import java.lang.reflect.Constructor;

public class Overloading {

    public static <T> T get(Class<T> serverService) throws ReflectiveOperationException {
        Class<?> implClass = Overloading.class.getClassLoader().loadClass(serverService.getName() + "Impl");
        Constructor<?> constructor = implClass.getConstructor();
        return (T) constructor.newInstance();
    }

}
