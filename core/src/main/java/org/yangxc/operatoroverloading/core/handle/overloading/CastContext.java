package org.yangxc.operatoroverloading.core.handle.overloading;

import org.yangxc.operatoroverloading.core.constant.CastType;

public record CastContext(String from, CastType type, String name, String to) {
}
