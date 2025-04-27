package org.yangxc.example;

import org.yangxc.core.annotation.OperatorFunction;
import org.yangxc.core.annotation.OperatorService;

@OperatorService
public interface BaseService {

    @OperatorFunction("1+2-3+4")
    int add();

    @OperatorFunction("a+b")
    int add(int a, int b);

}
