package org.yangxc.operatoroverloading.core.handle.overloading;

import org.yangxc.operatoroverloading.core.constant.CastMethodType;

public record CastContext(String from, CastMethodType type, String className, String name, String to) {
}
