package org.yangxc.core.handle.overloading;

import org.yangxc.core.constant.CastType;

public record CastContext(String from, CastType type, String name, String to) {
}
