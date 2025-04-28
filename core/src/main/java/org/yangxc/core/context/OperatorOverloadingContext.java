package org.yangxc.core.context;

import org.yangxc.core.annotation.Operator;

public record OperatorOverloadingContext(Operator operator, String name, String paramType, String resultType) {

}
